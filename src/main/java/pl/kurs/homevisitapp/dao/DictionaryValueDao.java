package pl.kurs.homevisitapp.dao;

import org.springframework.stereotype.Repository;
import pl.kurs.homevisitapp.models.DictionaryValue;

import javax.persistence.*;
import javax.transaction.Transactional;

@Repository
@Transactional
public class DictionaryValueDao implements IDictionaryValueDao {

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Override
    public DictionaryValue save(DictionaryValue dictionaryValue) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(dictionaryValue);
        tx.commit();
        entityManager.close();
        return dictionaryValue;
    }

    @Override
    public boolean existsByName(String name) {
        EntityManager entityManager = factory.createEntityManager();
        try {
            TypedQuery<Long> tq = entityManager.createQuery("SELECT COUNT(dv) FROM DictionaryValue dv " +
                    "WHERE dv.name = :name", Long.class)
                    .setParameter("name", name);
            Long count = tq.getSingleResult();
            return count > 0;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public DictionaryValue findByName(String name) {
        EntityManager entityManager = factory.createEntityManager();
        try {
            TypedQuery<DictionaryValue> tq = entityManager.createQuery("SELECT dv FROM DictionaryValue dv " +
                    "WHERE dv.name = :name", DictionaryValue.class)
                    .setParameter("name", name);
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

}
