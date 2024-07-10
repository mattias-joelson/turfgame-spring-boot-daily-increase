package org.joelson.turf.dailyinc.model;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, VisitId> {

    @Query("select v from Visit v order by v.time, v.zone.id, v.type, v.user.id limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetween(int offset, int limit, Class<T> type);

    @Query("select v from Visit v order by v.time desc, v.zone.id desc, v.type desc, v.user.id desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversed(int limit, Class<T> type);

    @Query("select v from Visit v where v.user.id = :userId order by v.time limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetweenByUser(Long userId, int offset, int limit, Class<T> type);

    @Query("select v from Visit v where v.user.id = :userId order by v.time desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversedByUser(Long userId, int limit, Class<T> type);

    @Query("select v from Visit v where v.zone.id = :zoneId order by v.time, v.type, v.user.id limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetweenByZone(Long zoneId, int offset, int limit, Class<T> type);

    @Query("select v from Visit v where v.zone.id = :zoneId order by v.time desc, v.type desc, v.user.id desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversedByZone(Long zoneId, int limit, Class<T> type);
}
