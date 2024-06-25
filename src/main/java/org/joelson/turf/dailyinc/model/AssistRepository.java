package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssistRepository extends JpaRepository<Assist, AssistId> {

    Optional<Assist> findByVisitAndUser(Visit visit, User user);
}
