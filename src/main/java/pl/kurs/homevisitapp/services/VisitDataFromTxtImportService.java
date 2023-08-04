package pl.kurs.homevisitapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kurs.homevisitapp.dao.IDoctorDao;
import pl.kurs.homevisitapp.dao.IHomeVisitDao;
import pl.kurs.homevisitapp.dao.IPatientDao;
import pl.kurs.homevisitapp.exceptions.EmptyFileException;
import pl.kurs.homevisitapp.models.Doctor;
import pl.kurs.homevisitapp.models.HomeVisit;
import pl.kurs.homevisitapp.models.Patient;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class VisitDataFromTxtImportService implements IFileDataImporter {

    private final IDoctorDao doctorDao;
    private final IPatientDao patientDao;
    private final IHomeVisitDao homeVisitDao;
    private final String filePath;

    public VisitDataFromTxtImportService(IDoctorDao doctorDao, IPatientDao patientDao, IHomeVisitDao homeVisitDao,
                                         @Value("${file.visits}") String filePath) {
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
        this.homeVisitDao = homeVisitDao;
        this.filePath = filePath;
    }

    @Override
    public void importData() {
        importDataFromFile(new File(filePath));
    }

    @Transactional
    public void importDataFromFile(File file) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            if (lines.size() == 0) {
                throw new EmptyFileException("File is empty.");
            }
            lines.stream()
                    .skip(1)
                    .map(line -> line.split("\t"))
                    .forEach(x -> {
                        Long doctorId = Long.parseLong(x[0]);
                        Long patientId = Long.parseLong(x[1]);
                        LocalDate visitDate = LocalDate.parse(x[2], dateFormatter);

                        Doctor doctor = Optional.ofNullable(doctorDao.findById(doctorId))
                                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
                        Patient patient = Optional.ofNullable(patientDao.findById(patientId))
                                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

                        HomeVisit homeVisit = new HomeVisit();
                        homeVisit.setDoctor(doctor);
                        homeVisit.setPatient(patient);
                        homeVisit.setVisitDate(visitDate);
                        homeVisitDao.save(homeVisit);
                    });
        } catch (IOException | EmptyFileException e) {
            System.err.println("Error while importing data: " + e.getMessage());
        }
    }

}

