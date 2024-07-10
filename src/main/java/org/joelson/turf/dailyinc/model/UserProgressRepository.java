package org.joelson.turf.dailyinc.model;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<Progress, ProgressId> {

    @Query("select up from Progress up order by up.user.id, up.date limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetween(int offset, int limit, Class<T> type);

    @Query("select up from Progress up order by up.user.id desc, up.date desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversed(int limit, Class<T> type);

    @Query("select up from Progress up where up.user.id = :userId order by up.date limit :limit offset :offset")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetweenByUser(Long userId, int offset, int limit, Class<T> type);

    @Query("select up from Progress up where up.user.id = :userId order by up.date desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversedByUser(Long userId, int limit, Class<T> type);
}
