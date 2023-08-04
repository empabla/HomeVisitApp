package pl.kurs.homevisitapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kurs.homevisitapp.dao.IPatientDao;
import pl.kurs.homevisitapp.exceptions.EmptyFileException;
import pl.kurs.homevisitapp.models.Patient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientDataFromTxtImportService implements IFileDataImporter {

    private final IPatientDao patientDao;
    private final String filePath;

    public PatientDataFromTxtImportService(IPatientDao patientDao, @Value("${file.patients}") String filePath) {
        this.patientDao = patientDao;
        this.filePath = filePath;
    }

    @Override
    public void importData() {
        importDataFromFile(new File(filePath));
    }

    private void importDataFromFile(File file) {
        List<Patient> patientsToSave = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            if (lines.size() == 0) {
                throw new EmptyFileException("File is empty.");
            }
            lines.stream()
                    .skip(1)
                    .map(x -> x.split("\t"))
                    .forEach(x -> {
                        Patient patient = new Patient();
                        patient.setId(Long.valueOf(x[0]));
                        patient.setLastName(x[1]);
                        patient.setFirstName(x[2]);
                        patient.setPesel(x[3]);
                        patient.setBirthDate(LocalDate.parse(x[4], dateFormatter));
                        patientsToSave.add(patient);
                    });
            patientDao.saveAll(patientsToSave);
        } catch (IOException | EmptyFileException e) {
            System.err.println("Error while importing data: " + e.getMessage());
        }
    }

}
