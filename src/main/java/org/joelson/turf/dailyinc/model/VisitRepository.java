package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, VisitId> {

    List<Visit> findAllByUser(User user);

    List<Visit> findAllByZone(Zone zone);
}
