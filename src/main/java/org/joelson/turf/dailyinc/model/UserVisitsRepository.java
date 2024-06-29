package org.joelson.turf.dailyinc.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserVisitsRepository extends JpaRepository<UserVisits, UserVisitsId> {

    @Query("select uv from UserVisits uv order by uv.user.id, uv.date")
    <T> List<T> findAllSorted(Class<T> type);

    @Query("select uv from UserVisits uv where uv.user.id = :userId order by uv.date")
    <T> List<T> findAllSortedByUser(Long userId, Class<T> type);
}
