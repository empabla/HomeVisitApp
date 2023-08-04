package pl.kurs.homevisitapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.kurs.homevisitapp.config.JpaConfig;
import pl.kurs.homevisitapp.dao.PatientDao;
import pl.kurs.homevisitapp.models.Patient;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfig.class, PatientDao.class})
public class InMemoryDBTest {

    @Autowired
    private PatientDao patientDao;

    @Test
    public void shouldReturnPatientWithId1FromDbSameAsSavedOne() {
        Patient patient = new Patient(
                "Kowalski",
                "Jan",
                LocalDate.of(1990, 1, 1),
                "90010111111",
                1L
        );

        patientDao.save(patient);

        Patient patientFromDb = patientDao.findById(1L);

        assertEquals("Kowalski", patientFromDb.getLastName());
        assertEquals("Jan", patientFromDb.getFirstName());
        assertEquals(LocalDate.of(1990, 1, 1), patientFromDb.getBirthDate());
        assertEquals("90010111111", patientFromDb.getPesel());
    }

}
