package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, UserProgressId> {

    List<UserProgress> findAllByUser(User user);
}
