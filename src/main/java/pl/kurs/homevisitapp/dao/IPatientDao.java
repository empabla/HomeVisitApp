package pl.kurs.homevisitapp.dao;

import pl.kurs.homevisitapp.models.Patient;

import java.util.List;

public interface IPatientDao {

    Patient save(Patient patient);

    List<Patient> saveAll(List<Patient> patients);

    Patient findById(Long patientId);

    Patient findByIdWithVisits(Long id);

}
