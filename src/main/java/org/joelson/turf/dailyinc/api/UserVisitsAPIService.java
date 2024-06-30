package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserVisits;
import org.joelson.turf.dailyinc.model.UserVisitsId;
import org.joelson.turf.dailyinc.model.UserVisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserVisitsAPIService {

    @Autowired
    UserVisitsRepository userVisitsRepository;

    public <T> List<T> getSortedUserVisits(Class<T> type) {
        return userVisitsRepository.findAllSorted(type);
    }

    public <T> List<T> getSortedUserVisitsByUser(Long userId, Class<T> type) {
        return userVisitsRepository.findAllSortedByUser(userId, type);
    }

    private UserVisits findByUserAndDate(User user, Instant date) {
        return userVisitsRepository.findById(new UserVisitsId(user.getId(), date)).orElse(null);
    }

    public int increaseUserVisits(User user, Instant date) {
        UserVisits userVisits = findByUserAndDate(user, date);
        if (userVisits == null) {
            userVisitsRepository.save(new UserVisits(user, date, 1));
            return 1;
        } else {
            int visits = userVisits.getVisits() + 1;
            userVisits.setVisits(visits);
            userVisitsRepository.save(userVisits);
            return visits;
        }
    }
}
