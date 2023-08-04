package pl.kurs.homevisitapp.dao;

import pl.kurs.homevisitapp.models.Doctor;

import java.time.LocalDate;
import java.util.List;

public interface IDoctorDao {

    Doctor save(Doctor doctor);

    List<Doctor> saveAll(List<Doctor> doctors);

    Doctor findById(Long doctorId);

    LocalDate findFirstAvailableVisitAfterDateById(Long id, LocalDate startDate);

}
