package org.joelson.turf.dailyinc.model;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query("select z from Zone z where z.id >= :minId and z.id <= :maxId order by z.id asc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findAllSortedBetween(Long minId, Long maxId, int limit, Class<T> type);

    @Query("select z from Zone z order by z.id desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversed(int limit, Class<T> type);

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Optional<T> findByName(String name, Class<T> type);
}
