package org.joelson.turf.dailyinc.model;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, ProgressId> {

    @Query("select p from Progress p order by p.user.id, p.date limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetween(int offset, int limit, Class<T> type);

    @Query("select p from Progress p order by p.user.id desc, p.date desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversed(int limit, Class<T> type);

    @Query("select p from Progress p where p.user.id = :userId order by p.date limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetweenByUser(Long userId, int offset, int limit, Class<T> type);

    @Query("select p from Progress p where p.user.id = :userId order by p.date desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversedByUser(Long userId, int limit, Class<T> type);
}
