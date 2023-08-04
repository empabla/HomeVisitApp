package pl.kurs.homevisitapp.dao;

import org.springframework.stereotype.Repository;
import pl.kurs.homevisitapp.models.Doctor;
import pl.kurs.homevisitapp.models.HomeVisit;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class DoctorDao implements IDoctorDao {

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Override
    public Doctor save(Doctor doctor) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(doctor);
        tx.commit();
        entityManager.close();
        return doctor;
    }

    @Override
    public List<Doctor> saveAll(List<Doctor> doctors) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        doctors.forEach(entityManager::persist);
        tx.commit();
        entityManager.close();
        return doctors;
    }

    @Override
    public Doctor findById(Long doctorId) {
        EntityManager entityManager = factory.createEntityManager();
        Doctor doctor = entityManager.find(Doctor.class, doctorId);
        entityManager.close();
        return doctor;
    }

    @Override
    public LocalDate findFirstAvailableVisitAfterDateById(Long id, LocalDate startDate) {
        EntityManager entityManager = factory.createEntityManager();
        try {
            Doctor doctor = entityManager.createQuery("SELECT d FROM Doctor d LEFT JOIN FETCH d.specialization s " +
                    "LEFT JOIN FETCH s.dictionary LEFT JOIN FETCH d.homeVisits hv LEFT JOIN FETCH hv.patient " +
                    "WHERE d.id = :doctorId", Doctor.class)
                    .setParameter("doctorId", id)
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Doctor with id: " + id + " not found"));
            List<LocalDate> bookedDates = doctor.getHomeVisits()
                    .stream()
                    .map(HomeVisit::getVisitDate)
                    .collect(Collectors.toList());
            LocalDate nextAvailableDate = startDate.plusDays(1);
            while (bookedDates.contains(nextAvailableDate)) {
                nextAvailableDate = nextAvailableDate.plusDays(1);
            }
            return nextAvailableDate;
        } finally {
            entityManager.close();
        }
    }

}
