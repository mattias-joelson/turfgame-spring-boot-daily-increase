package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitRepository;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.model.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class VisitService {

    Logger logger = LoggerFactory.getLogger(VisitService.class);

    @Autowired
    VisitRepository visitRepository;

    public List<Visit> getVisits() {
        return visitRepository.findAll();
    }

    public Visit getVisit(Zone zone, User user, Instant time) {
        return visitRepository.findByZoneAndUserAndTime(zone, user, time).orElse(null);
    }

    public Visit getOrCreate(Zone zone, User user, Instant time, VisitType type) {
        return visitRepository.findByZoneAndUserAndTime(zone, user, time)
                .orElseGet(() -> visitRepository.save(new Visit(zone, user, time, type)));
    }
}
