package org.joelson.turf.dailyinc.model;

//import org.hibernate.query.spi.Limit;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, VisitId> {

//    @Query("select v from Visit v order by v.time, v.zone.id, v.type, v.user.id limit :limit")
//    <T> List<T> findAllSorted(Class<T> type, Limit limit);

//    @Query("select v from Visit v order by v.time, v.zone.id, v.type, v.user.id limit :limit")
//    <T> List<T> findAllSortedLimit(int limit, Class<T> type);

//    List<Visit> findByOrderByTimeAsc(Limit limit);

//    @Query("select v from Visit v order by v.time, v.zone.id, v.type, v.user.id")
//    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
//    <T> List<T> findAllSorted(Limit limit, Class<T> type);

    @Query("select v from Visit v order by v.time, v.zone.id, v.type, v.user.id limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findAllSorted(int offset, int limit, Class<T> type);

    @Query("select v from Visit v order by v.time desc, v.zone.id desc, v.type, v.user.id limit :count")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findAllSortedReversed(int count, Class<T> type);

    @Query("select v from Visit v where v.user.id = :userId order by v.time")
    <T> List<T> findAllSortedByUser(Long userId, Class<T> type);

    @Query("select v from Visit v where v.zone.id = :zoneId order by v.time, v.type, v.user.id")
    <T> List<T> findAllSortedByZone(Long zoneId, Class<T> type);
}
