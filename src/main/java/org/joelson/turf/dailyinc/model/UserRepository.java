package org.joelson.turf.dailyinc.model;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id >= :minId and u.id <= :maxId order by u.id asc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findSortedBetween(Long minId, Long maxId, int limit, Class<T> type);

    @Query("select u from User u order by u.id desc limit :limit")
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "100"))
    <T> List<T> findLastSortedReversed(int limit, Class<T> type);

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Optional<T> findByName(String name, Class<T> type);
}
