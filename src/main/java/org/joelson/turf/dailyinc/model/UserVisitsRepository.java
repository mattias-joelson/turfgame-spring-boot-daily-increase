package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVisitsRepository extends JpaRepository<UserVisits, UserVisitsId> {

    List<UserVisits> findAllByUser(User user);
}
