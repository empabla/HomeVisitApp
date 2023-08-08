package pl.kurs.homevisitapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.kurs.homevisitapp.config.JpaConfig;
import pl.kurs.homevisitapp.dao.*;
import pl.kurs.homevisitapp.models.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {JpaConfig.class, PatientDao.class, DoctorDao.class, HomeVisitDao.class,
        DictionaryDao.class, DictionaryValueDao.class})
public class HomeVisitAppTest {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private HomeVisitDao homeVisitDao;

    @Autowired
    private DictionaryDao dictionaryDao;

    @Autowired
    private DictionaryValueDao dictionaryValueDao;

    @BeforeAll
    public static void init(@Autowired JdbcTemplate jdbcTemplate) {
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(new ClassPathResource("test-data.sql"));
        rdp.execute(jdbcTemplate.getDataSource());
    }

    @Test
    public void shouldReturnSpecializationsDictionaryByName() {
        //given
        String dictionaryNameForTest = "specializations";

        //when
        Dictionary resultDictionary = dictionaryDao.findByName(dictionaryNameForTest);

        //then
        assertNotNull(resultDictionary);
        assertEquals(dictionaryNameForTest, resultDictionary.getName());
    }

    @Test
    public void shouldReturnNullWhenFindByNameNotExistingDictionary() {
        //given
        String dictionaryNameForTest = "notExistingDictionary";

        //when
        Dictionary resultDictionary = dictionaryDao.findByName(dictionaryNameForTest);

        //then
        assertNull(resultDictionary);
    }

    @Test
    public void shouldReturnKardiologSpecializationDictionaryValueByName() {
        //given
        String dictionaryValueForTest = "kardiolog";

        //when
        DictionaryValue resultValue = dictionaryValueDao.findByName(dictionaryValueForTest);

        //then
        assertNotNull(resultValue);
        assertEquals(dictionaryValueForTest, resultValue.getName());
    }

    @Test
    public void shouldReturnNullWhenFindByNameNotExistingDictionaryValue() {
        //given
        String dictionaryValueNameForTest = "notExistingDictionaryValue";

        //when
        DictionaryValue resultDictionaryValue = dictionaryValueDao.findByName(dictionaryValueNameForTest);

        //then
        assertNull(resultDictionaryValue);
    }

    @Test
    public void shouldReturnTrueForChirurgSpecializationDictionaryVlaues() {
        //given
        String dictionaryValueForTest = "chirurg";

        //when
        boolean result = dictionaryValueDao.existsByName(dictionaryValueForTest);

        //then
        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseForNotExistingDictionaryVlaues() {
        //given
        String dictionaryValueForTest = "notExistingDictionaryValue";

        //when
        boolean result = dictionaryValueDao.existsByName(dictionaryValueForTest);

        //then
        assertFalse(result);
    }

    @Test
    public void shouldReturnPatientWithId1FromDb() {
        //given
        Long patientIdForTest = 1L;

        //when
        Patient patientFromDb = patientDao.findById(patientIdForTest);

        //then
        assertEquals("Madej", patientFromDb.getLastName());
        assertEquals("Karol", patientFromDb.getFirstName());
        assertEquals(LocalDate.of(1985, 10, 10), patientFromDb.getBirthDate());
        assertEquals("85101012345", patientFromDb.getPesel());
    }

    @Test
    public void shouldReturnDoctorWithId1FromDb() {
        //given
        Long doctorIdForTest = 1L;

        //when
        Doctor doctorFromDb = doctorDao.findById(doctorIdForTest);

        //then
        assertEquals("Nowak", doctorFromDb.getLastName());
        assertEquals("Adam", doctorFromDb.getFirstName());
        assertEquals(LocalDate.of(1980, 1, 1), doctorFromDb.getBirthDate());
        assertEquals("80010112345", doctorFromDb.getPesel());
    }

    @Test
    public void shouldReturnVisitWithId1FromDb() {
        //given
        Long visitIdForTest = 1L;

        //when
        HomeVisit visitFromDb = homeVisitDao.findById(visitIdForTest);

        //then
        assertEquals("Madej", visitFromDb.getPatient().getLastName());
        assertEquals("Karol", visitFromDb.getPatient().getFirstName());
        assertEquals(LocalDate.of(1985, 10, 10), visitFromDb.getPatient().getBirthDate());
        assertEquals("85101012345", visitFromDb.getPatient().getPesel());
        assertEquals("Nowak", visitFromDb.getDoctor().getLastName());
        assertEquals("Adam", visitFromDb.getDoctor().getFirstName());
        assertEquals(LocalDate.of(1980, 1, 1), visitFromDb.getDoctor().getBirthDate());
        assertEquals("80010112345", visitFromDb.getDoctor().getPesel());
    }

    @Test
    public void shouldReturnPatientWithId1WithVisitsWithId1And2() {
        //given
        Long patientIdForTest = 1L;

        //when
        Patient patientFromDbWithVisits = patientDao.findByIdWithVisits(patientIdForTest);

        //then
        assertEquals("Madej", patientFromDbWithVisits.getLastName());
        assertEquals("Karol", patientFromDbWithVisits.getFirstName());
        assertEquals(LocalDate.of(1985, 10, 10), patientFromDbWithVisits.getBirthDate());
        assertEquals("85101012345", patientFromDbWithVisits.getPesel());
        assertEquals(2, patientFromDbWithVisits.getHomeVisits().size());
        assertTrue(patientFromDbWithVisits.getHomeVisits().contains(homeVisitDao.findById(1L)));
        assertTrue(patientFromDbWithVisits.getHomeVisits().contains(homeVisitDao.findById(2L)));
    }

    @Test
    public void shouldReturnDate20230804ForDoctorWithId2AsFirstAvailableVisitAfter20230801() {
        //given
        Long doctorIdForTest = 2L;
        LocalDate dateForTest = LocalDate.of(2023, 8, 1);

        //when
        LocalDate firstAvailableDate = doctorDao
                .findFirstAvailableVisitAfterDateById(doctorIdForTest, dateForTest);

        //then
        assertEquals(LocalDate.of(2023, 8, 4), firstAvailableDate);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenCheckAvailableVisitForDoctorWithId4() {
        //given
        Long doctorIdForTest = 4L;
        LocalDate dateForTest = LocalDate.of(2023, 8, 1);

        //when&then
        Exception e = assertThrows(EntityNotFoundException.class,
                () -> doctorDao.findFirstAvailableVisitAfterDateById(doctorIdForTest, dateForTest));
        assertNotNull(e);
        assertEquals("Doctor with id: 4 not found", e.getMessage());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenFindPatientWithId4() {
        //given
        Long patientIdForTest = 4L;

        //when&then
        Exception e = assertThrows(EntityNotFoundException.class, () -> patientDao.findByIdWithVisits(patientIdForTest));
        assertNotNull(e);
        assertEquals("Patient with id: 4 not found", e.getMessage());
    }

}
