package pl.kurs.homevisitapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kurs.homevisitapp.dao.IDictionaryDao;
import pl.kurs.homevisitapp.dao.IDictionaryValueDao;
import pl.kurs.homevisitapp.dao.IDoctorDao;
import pl.kurs.homevisitapp.exceptions.EmptyFileException;
import pl.kurs.homevisitapp.models.Dictionary;
import pl.kurs.homevisitapp.models.DictionaryValue;
import pl.kurs.homevisitapp.models.Doctor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DoctorDataFromTxtImportService implements IFileDataImporter {

    private final IDoctorDao doctorDao;
    private final String filePath;
    private final IDictionaryValueDao dictionaryValueDao;
    private final IDictionaryDao dictionaryDao;

    public DoctorDataFromTxtImportService(IDoctorDao doctorDao, @Value("${file.doctors}") String filePath,
                                          IDictionaryValueDao dictionaryValueDao, IDictionaryDao dictionaryDao) {
        this.doctorDao = doctorDao;
        this.filePath = filePath;
        this.dictionaryValueDao = dictionaryValueDao;
        this.dictionaryDao = dictionaryDao;
    }

    @Override
    public void importData() {
        importDataFromFile(new File(filePath));
    }

    private void importDataFromFile(File file) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        try {
            Dictionary dictionary = getOrCreateDictionary("specializations");
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            if (lines.size() == 0) {
                throw new EmptyFileException("File is empty.");
            }
            lines.stream()
                    .skip(1)
                    .map(x -> x.split("\t"))
                    .forEach(x -> {
                        Doctor doctor = new Doctor();
                        doctor.setId(Long.valueOf(x[0]));
                        doctor.setLastName(x[1]);
                        doctor.setFirstName(x[2]);
                        String specializationName = x[3];
                        if (!dictionaryValueDao.existsByName(specializationName)) {
                            DictionaryValue newSpecialization = new DictionaryValue(specializationName, dictionary);
                            dictionaryValueDao.save(newSpecialization);
                        }
                        DictionaryValue specialization = dictionaryValueDao.findByName(specializationName);
                        doctor.setSpecialization(specialization);
                        doctor.setBirthDate(LocalDate.parse(x[4], dateFormatter));
                        doctor.setNip(x[5]);
                        doctor.setPesel(x[6]);
                        doctorDao.save(doctor);
                    });
        } catch (IOException | EmptyFileException e) {
            System.err.println("Error while importing data: " + e.getMessage());
        }
    }

    private Dictionary getOrCreateDictionary(String dictionaryName) {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName);
        if (dictionary == null) {
            dictionary = new Dictionary(dictionaryName);
            dictionaryDao.save(dictionary);
        }
        return dictionary;
    }

}