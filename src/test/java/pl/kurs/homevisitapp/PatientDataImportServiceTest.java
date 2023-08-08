package pl.kurs.homevisitapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.kurs.homevisitapp.dao.IPatientDao;
import pl.kurs.homevisitapp.models.Patient;
import pl.kurs.homevisitapp.services.PatientDataFromTxtImportService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class PatientDataImportServiceTest {

    @Mock
    IPatientDao patientDaoMock;

    PatientDataFromTxtImportService importService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSavePatientsFromFileWhenCorrectContent() {
        //given
        String filePathForTest = "src/test/resources/test-patients.txt";
        importService = new PatientDataFromTxtImportService(patientDaoMock, filePathForTest);

        //when
        importService.importData();

        //then
        verify(patientDaoMock).saveAll(anyList());
    }

    @Test
    public void shouldParseLinesAndCreatePatients() throws IOException {
        // given
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

        String line1 = "Id_pacjenta\tNazwisko\tImie\tPESEL\tData_urodzenia";
        String line2 = "1\tMadej\tKarol\t85101012345\t1985-10-10";
        String line3 = "2\tWnuk\tKalina\t95121212345\t1995-12-12";
        List<String> lines = Arrays.asList(line1, line2, line3);

        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }

        importService = new PatientDataFromTxtImportService(patientDaoMock, tempFile.getAbsolutePath());

        // when
        importService.importData();

        // then
        ArgumentCaptor<List<Patient>> patientCaptor = ArgumentCaptor.forClass(List.class);
        verify(patientDaoMock).saveAll(patientCaptor.capture());

        List<Patient> capturedPatients = patientCaptor.getValue();
        assertThat(capturedPatients).hasSize(2);

        Patient patient1 = capturedPatients.get(0);
        assertThat(patient1.getId()).isEqualTo(1L);
        assertThat(patient1.getLastName()).isEqualTo("Madej");
        assertThat(patient1.getFirstName()).isEqualTo("Karol");
        assertThat(patient1.getPesel()).isEqualTo("85101012345");
        assertThat(patient1.getBirthDate()).isEqualTo(LocalDate.parse("1985-10-10", dateFormatter));

        Patient patient2 = capturedPatients.get(1);
        assertThat(patient2.getId()).isEqualTo(2L);
        assertThat(patient2.getLastName()).isEqualTo("Wnuk");
        assertThat(patient2.getFirstName()).isEqualTo("Kalina");
        assertThat(patient2.getPesel()).isEqualTo("95121212345");
        assertThat(patient2.getBirthDate()).isEqualTo(LocalDate.parse("1995-12-12", dateFormatter));

        verify(patientDaoMock, times(1)).saveAll(anyList());
    }

}