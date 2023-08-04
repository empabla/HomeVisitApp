package pl.kurs.homevisitapp.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import pl.kurs.homevisitapp.dao.IDoctorDao;
import pl.kurs.homevisitapp.dao.IHomeVisitDao;
import pl.kurs.homevisitapp.dao.IPatientDao;
import pl.kurs.homevisitapp.services.IFileDataImporter;

import java.time.LocalDate;

@ComponentScan(basePackages = "pl.kurs")
public class HomeVisitApp {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(HomeVisitApp.class);

        ctx.getBeansOfType(IFileDataImporter.class)
                .values()
                .forEach(IFileDataImporter::importData);

        IPatientDao patientDao = ctx.getBean(IPatientDao.class);
        IDoctorDao doctorDao = ctx.getBean(IDoctorDao.class);
        IHomeVisitDao homeVisitDao = ctx.getBean(IHomeVisitDao.class);

        System.out.println("patientDao.findById(100L) = " + patientDao.findById(100L));
        System.out.println("doctorDao.findById(25L) = " + doctorDao.findById(25L));
        System.out.println("homeVisitDao.findById(2L) = " + homeVisitDao.findById(2L));

        System.out.println("patientDao.findByIdWithVisits(100L) = " + patientDao.findByIdWithVisits(100L));
        System.out.println("doctorDao.findFirstAvailableVisitAfterDateById(23L, LocalDate.of(2006,9,23)) = " +
                doctorDao.findFirstAvailableVisitAfterDateById(23L, LocalDate.of(2006, 9, 23)));

    }
}

