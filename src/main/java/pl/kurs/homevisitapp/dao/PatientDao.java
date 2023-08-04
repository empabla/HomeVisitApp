package pl.kurs.homevisitapp.dao;

import org.springframework.stereotype.Repository;
import pl.kurs.homevisitapp.models.Patient;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class PatientDao implements IPatientDao {

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Override
    public Patient save(Patient patient) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(patient);
        tx.commit();
        entityManager.close();
        return patient;
    }

    @Override
    public List<Patient> saveAll(List<Patient> patients) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        patients.forEach(entityManager::persist);
        tx.commit();
        entityManager.close();
        return patients;
    }

    @Override
    public Patient findById(Long patientId) {
        EntityManager entityManager = factory.createEntityManager();
        Patient patient = entityManager.find(Patient.class, patientId);
        entityManager.close();
        return patient;
    }

    @Override
    public Patient findByIdWithVisits(Long id) {
        EntityManager entityManager = factory.createEntityManager();
        try {
            TypedQuery<Patient> tq = entityManager.createQuery
                    ("SELECT p FROM Patient p LEFT JOIN FETCH p.homeVisits hv LEFT JOIN FETCH hv.doctor d " +
                            "LEFT JOIN FETCH d.specialization s LEFT JOIN FETCH s.dictionary " +
                            "WHERE p.id = :patientId", Patient.class);
            tq.setParameter("patientId", id);
            return tq.getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Patient with id: " + id + " not found"));
        } finally {
            entityManager.close();
        }
    }

}
