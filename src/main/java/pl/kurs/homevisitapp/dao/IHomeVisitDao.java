package pl.kurs.homevisitapp.dao;

import pl.kurs.homevisitapp.models.HomeVisit;

import java.util.List;

public interface IHomeVisitDao {

    HomeVisit save(HomeVisit homeVisit);

    List<HomeVisit> saveAll(List<HomeVisit> homeVisits);

    HomeVisit findById(Long homeVisitId);

}
