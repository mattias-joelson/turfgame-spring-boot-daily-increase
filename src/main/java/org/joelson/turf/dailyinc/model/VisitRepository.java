package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, VisitId> {

    @Query("select v from Visit v order by v.time, v.zone.id, v.type, v.user.id")
    <T> List<T> findAllSorted(Class<T> type);

    @Query("select v from Visit v where v.user.id = :userId order by v.time")
    <T> List<T> findAllSortedByUser(Long userId, Class<T> type);

    @Query("select v from Visit v where v.zone.id = :zoneId order by v.time, v.type, v.user.id")
    <T> List<T> findAllSortedByZone(Long zoneId, Class<T> type);
}
