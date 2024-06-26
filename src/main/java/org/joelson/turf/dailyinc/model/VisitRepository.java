package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllByUser(User user);

    List<Visit> findAllByZone(Zone zone);

    Optional<Visit> findByZoneAndUserAndTime(Zone zone, User user, Instant time);
}
