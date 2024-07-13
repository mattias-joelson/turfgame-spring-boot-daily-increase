package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitId;
import org.joelson.turf.dailyinc.model.VisitRepository;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.model.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class VisitService {

    @Autowired
    UserService userService;

    @Autowired
    VisitRepository visitRepository;

    public Visit getVisit(Zone zone, User user, Instant time) {
        return visitRepository.findById(new VisitId(zone.getId(), user.getId(), time)).orElse(null);
    }

    public Visit create(Zone zone, User user, Instant time, VisitType type) {
        return visitRepository.save(new Visit(zone, user, time, type));
    }

    public List<User> findDistinctUserOrderById() {
        List<Long> distinctUserId = visitRepository.findDistinctUserIdOrderByUserId();
        return userService.toUsers(distinctUserId, User.class);
    }

    public List<Instant> findAllSortedVisitTimesByUser(User user) {
        return visitRepository.findTimeByUserOrderByTime(user);
    }
}
