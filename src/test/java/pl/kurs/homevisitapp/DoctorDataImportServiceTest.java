package pl.kurs.homevisitapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.kurs.homevisitapp.dao.IDictionaryDao;
import pl.kurs.homevisitapp.dao.IDictionaryValueDao;
import pl.kurs.homevisitapp.dao.IDoctorDao;
import pl.kurs.homevisitapp.models.Dictionary;
import pl.kurs.homevisitapp.models.DictionaryValue;
import pl.kurs.homevisitapp.models.Doctor;
import pl.kurs.homevisitapp.services.DoctorDataFromTxtImportService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DoctorDataImportServiceTest {

    @Mock
    IDoctorDao doctorDaoMock;

    @Mock
    IDictionaryValueDao dictionaryValueDaoMock;

    @Mock
    IDictionaryDao dictionaryDaoMock;

    DoctorDataFromTxtImportService importService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSavDoctorsFromFileWhenCorrectContent() {
        //given
        String filePathForTest = "src/test/resources/test-doctors.txt";
        importService = new DoctorDataFromTxtImportService(doctorDaoMock, filePathForTest, dictionaryValueDaoMock,
                dictionaryDaoMock);

        //when
        importService.importData();

        //then
        verify(doctorDaoMock).save(any());
    }

    @Test
    public void shouldParseLinesAndCreateDoctors() throws IOException {
        // given
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

        String line1 = "Id_lekarza\tNazwisko\tImie\tSpecjalnosc\tData_urodzenia\tNIP\tPESEL";
        String line2 = "1\tNowak\tAdam\tkardiolog\t1980-01-01\t123-456-11-11\t80010112345";
        String line3 = "2\tKowal\tAnna\tchirurg\t1990-02-02\t123-456-22-22\t90020212345";
        List<String> lines = Arrays.asList(line1, line2, line3);

        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }

        importService = new DoctorDataFromTxtImportService(doctorDaoMock, tempFile.getAbsolutePath(),
                dictionaryValueDaoMock, dictionaryDaoMock);

        DictionaryValue specializationKardiolog = new DictionaryValue("kardiolog",
                new Dictionary("specializations"));
        when(dictionaryValueDaoMock.findByName("kardiolog")).thenReturn(specializationKardiolog);
        DictionaryValue specializationChirurg = new DictionaryValue("chirurg",
                new Dictionary("specializations"));
        when(dictionaryValueDaoMock.findByName("chirurg")).thenReturn(specializationChirurg);

        // when
        importService.importData();

        // then
        ArgumentCaptor<Doctor> doctorCaptor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorDaoMock, times(2)).save(doctorCaptor.capture());

        List<Doctor> capturedDoctors = doctorCaptor.getAllValues();
        assertThat(capturedDoctors).hasSize(2);

        Doctor doctor1 = capturedDoctors.get(0);
        assertThat(doctor1.getId()).isEqualTo(1L);
        assertThat(doctor1.getLastName()).isEqualTo("Nowak");
        assertThat(doctor1.getFirstName()).isEqualTo("Adam");
        assertThat(doctor1.getSpecialization().getName()).isEqualTo("kardiolog");
        assertThat(doctor1.getBirthDate()).isEqualTo(LocalDate.parse("1980-01-01", dateFormatter));
        assertThat(doctor1.getNip()).isEqualTo("123-456-11-11");
        assertThat(doctor1.getPesel()).isEqualTo("80010112345");

        Doctor doctor2 = capturedDoctors.get(1);
        assertThat(doctor2.getId()).isEqualTo(2L);
        assertThat(doctor2.getLastName()).isEqualTo("Kowal");
        assertThat(doctor2.getFirstName()).isEqualTo("Anna");
        assertThat(doctor2.getSpecialization().getName()).isEqualTo("chirurg");
        assertThat(doctor2.getBirthDate()).isEqualTo(LocalDate.parse("1990-02-02", dateFormatter));
        assertThat(doctor2.getNip()).isEqualTo("123-456-22-22");
        assertThat(doctor2.getPesel()).isEqualTo("90020212345");

        verify(doctorDaoMock, times(2)).save(any(Doctor.class));
    }

}
