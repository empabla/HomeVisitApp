package pl.kurs.homevisitapp.dao;

import org.springframework.stereotype.Repository;
import pl.kurs.homevisitapp.models.Dictionary;

import javax.persistence.*;
import javax.transaction.Transactional;

@Repository
@Transactional
public class DictionaryDao implements IDictionaryDao {

    @PersistenceUnit
    private EntityManagerFactory factory;

    @Override
    public Dictionary findByName(String name) {
        EntityManager entityManager = factory.createEntityManager();
        try {
            TypedQuery<Dictionary> tq = entityManager.createQuery("SELECT d FROM Dictionary d " +
                    "WHERE d.name = :name", Dictionary.class)
                    .setParameter("name", name);
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Dictionary save(Dictionary dictionary) {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        entityManager.persist(dictionary);
        tx.commit();
        entityManager.close();
        return dictionary;
    }

}
