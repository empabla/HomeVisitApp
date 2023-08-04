package pl.kurs.homevisitapp.dao;

import pl.kurs.homevisitapp.models.Dictionary;

public interface IDictionaryDao {

    Dictionary findByName(String name);

    Dictionary save(Dictionary dictionary);

}
