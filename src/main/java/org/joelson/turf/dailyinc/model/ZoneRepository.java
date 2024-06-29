package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query("select z from Zone z order by z.id")
    <T> List<T> findAllSorted(Class<T> type);

    <T> Optional<T> findById(Long id, Class<T> type);

    <T> Optional<T> findByName(String name, Class<T> type);
}
