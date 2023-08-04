package pl.kurs.homevisitapp.dao;

import pl.kurs.homevisitapp.models.DictionaryValue;

public interface IDictionaryValueDao {

    DictionaryValue save(DictionaryValue dictionaryValue);

    boolean existsByName(String name);

    DictionaryValue findByName(String name);

}
