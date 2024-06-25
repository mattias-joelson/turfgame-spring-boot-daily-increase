package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Assist;
import org.joelson.turf.dailyinc.model.AssistRepository;
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
    AssistRepository assistRepository;

    @Autowired
    VisitRepository visitRepository;

    public List<Visit> getVisits() {
        return visitRepository.findAll();
    }

    public Visit getVisit(Zone zone, Instant time) {
        return visitRepository.findByZoneAndTime(zone, time).orElse(null);
    }

    public Assist getAssist(Visit visit, User user) {
        return assistRepository.findByVisitAndUser(visit, user).orElse(null);
    }

    public Visit createVisit(Zone zone, User user, Instant time, boolean take) {
        VisitType type = (take) ? VisitType.TAKEOVER : VisitType.REVISIT;
        Visit visit = getVisit(zone, time);logger.info("*** visit find done");
        if (visit != null) {
            throw new IllegalArgumentException(
                    String.format("Visit already exists for zone %s and time %s.", zone, time));
        }
        return visitRepository.save(new Visit(zone, user, time, type));
    }

    public Assist addAssist(Visit visit, User user) {
        Assist assist = getAssist(visit, user);
        if (assist != null) {
            throw new IllegalArgumentException(
                    String.format("Visit already exists for vise %s and user %s.", visit, user));
        }
        Assist entity = new Assist(visit, user);
        return assistRepository.save(entity);
    }
}
