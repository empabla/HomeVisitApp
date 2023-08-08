package pl.kurs.homevisitapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.kurs.homevisitapp.dao.IDoctorDao;
import pl.kurs.homevisitapp.dao.IHomeVisitDao;
import pl.kurs.homevisitapp.dao.IPatientDao;
import pl.kurs.homevisitapp.models.Doctor;
import pl.kurs.homevisitapp.models.HomeVisit;
import pl.kurs.homevisitapp.models.Patient;
import pl.kurs.homevisitapp.services.VisitDataFromTxtImportService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class VisitDataImportServiceTest {

    @Mock
    IHomeVisitDao homeVisitDaoMock;

    @Mock
    IDoctorDao doctorDaoMock;

    @Mock
    IPatientDao patientDaoMock;

    VisitDataFromTxtImportService importService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSaveHomeVisitsFromFileWhenCorrectContent() {
        //given
        String filePathForTest = "src/test/resources/test-visits.txt";
        importService = new VisitDataFromTxtImportService(doctorDaoMock, patientDaoMock, homeVisitDaoMock,
                filePathForTest);
        Doctor mockDoctor = new Doctor();
        Patient mockPatient = new Patient();
        when(doctorDaoMock.findById(anyLong())).thenReturn(mockDoctor);
        when(patientDaoMock.findById(anyLong())).thenReturn(mockPatient);

        //when
        importService.importData();

        //then
        verify(homeVisitDaoMock).save(any());
    }

    @Test
    public void shouldParseLinesAndCreateDoctors() throws IOException {
        // given
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

        String line1 = "Id_lekarza\tId_pacjenta\tData_wizyty";
        String line2 = "1\t1\t2023-08-01";
        String line3 = "1\t1\t2023-08-02";
        List<String> lines = Arrays.asList(line1, line2, line3);

        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }

        importService = new VisitDataFromTxtImportService(doctorDaoMock, patientDaoMock, homeVisitDaoMock,
                tempFile.getAbsolutePath());

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        Patient patient = new Patient();
        patient.setId(1L);
        when(doctorDaoMock.findById(1L)).thenReturn(doctor);
        when(patientDaoMock.findById(1L)).thenReturn(patient);

        // when
        importService.importData();

        // then
        ArgumentCaptor<HomeVisit> visitCaptor = ArgumentCaptor.forClass(HomeVisit.class);
        verify(homeVisitDaoMock, times(2)).save(visitCaptor.capture());

        List<HomeVisit> capturedVisits = visitCaptor.getAllValues();
        assertThat(capturedVisits).hasSize(2);

        HomeVisit homeVisit1 = capturedVisits.get(0);
        assertThat(homeVisit1.getPatient()).isEqualTo(patient);
        assertThat(homeVisit1.getDoctor()).isEqualTo(doctor);
        assertThat(homeVisit1.getVisitDate()).isEqualTo(LocalDate.parse("2023-08-01", dateFormatter));

        HomeVisit homeVisit2 = capturedVisits.get(1);
        assertThat(homeVisit2.getPatient()).isEqualTo(patient);
        assertThat(homeVisit2.getDoctor()).isEqualTo(doctor);
        assertThat(homeVisit2.getVisitDate()).isEqualTo(LocalDate.parse("2023-08-02", dateFormatter));

        verify(homeVisitDaoMock, times(2)).save(any(HomeVisit.class));
    }

}
