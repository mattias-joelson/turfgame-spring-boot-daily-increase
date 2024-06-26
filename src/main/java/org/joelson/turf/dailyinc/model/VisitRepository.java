package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByZoneAndUserAndTime(Zone zone, User user, Instant time);
}
