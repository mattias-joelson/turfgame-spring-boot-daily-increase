package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserVisits;
import org.joelson.turf.dailyinc.model.UserVisitsId;
import org.joelson.turf.dailyinc.model.UserVisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserVisitsService {

    @Autowired
    UserVisitsRepository userVisitsRepository;

    public UserVisits findByUserAndDate(User user, Instant date) {
        return userVisitsRepository.findById(new UserVisitsId(user.getId(), date)).orElse(null);
    }

    public int increaseUserVisits(User user, Instant date) {
        UserVisits userVisits = findByUserAndDate(user, date);
        if (userVisits == null) {
            userVisitsRepository.saveAndFlush(new UserVisits(user, date, 1));
            return 1;
        } else {
            int visits = userVisits.getVisits() + 1;
            userVisits.setVisits(visits);
            userVisitsRepository.saveAndFlush(userVisits);
            return visits;
        }
    }
}
