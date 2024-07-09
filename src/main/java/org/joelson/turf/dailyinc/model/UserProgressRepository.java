package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, UserProgressId> {

    @Query("select up from UserProgress up order by up.user.id, up.date")
    <T> List<T> findAllSorted(Class<T> type);

    @Query("select up from UserProgress up where up.user.id = :userId order by up.date")
    <T> List<T> findAllSortedByUser(Long userId, Class<T> type);
}
