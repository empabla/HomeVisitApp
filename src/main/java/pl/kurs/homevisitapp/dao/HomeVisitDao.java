package pl.kurs.homevisitapp.dao;

import org.springframework.stereotype.Repository;
import pl.kurs.homevisitapp.models.HomeVisit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class HomeVisitDao implements IHomeVisitDao {

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Override
    public HomeVisit save(HomeVisit homeVisit) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(homeVisit);
        tx.commit();
        entityManager.close();
        return homeVisit;
    }

    @Override
    public List<HomeVisit> saveAll(List<HomeVisit> homeVisits) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        homeVisits.forEach(entityManager::persist);
        tx.commit();
        entityManager.close();
        return homeVisits;
    }

    @Override
    public HomeVisit findById(Long homeVisitId) {
        EntityManager entityManager = factory.createEntityManager();
        HomeVisit homeVisit = entityManager.find(HomeVisit.class, homeVisitId);
        entityManager.close();
        return homeVisit;
    }

}
