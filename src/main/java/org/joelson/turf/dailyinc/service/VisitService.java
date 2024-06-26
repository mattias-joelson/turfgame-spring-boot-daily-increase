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

    private static int compareVisits(Visit o1, Visit o2) {
        int timeDiff = o1.getTime().compareTo(o2.getTime());
        if (timeDiff != 0) {
            return timeDiff;
        }
        int zoneIdDiff = o1.getZone().getId().compareTo(o2.getZone().getId());
        if (zoneIdDiff != 0) {
            return zoneIdDiff;
        }
        if (o1.getType() != o2.getType()) {
            return (o2.getType() == VisitType.ASSIST) ? -1 : 1;
        }
        return o1.getUser().getId().compareTo(o2.getUser().getId());
    }

    private static List<Visit> sortedByTimeAndZoneAndTypeAndUser(List<Visit> visits) {
        return visits.stream().sorted(VisitService::compareVisits).toList();
    }

    public List<Visit> getSortedVisits() {
        return sortedByTimeAndZoneAndTypeAndUser(visitRepository.findAll());
    }

    public List<Visit> getSortedVisitsByUser(User user) {
        return sortedByTimeAndZoneAndTypeAndUser(visitRepository.findAllByUser(user));
    }

    public List<Visit> getSortedVisitsByZone(Zone zone) {
        return sortedByTimeAndZoneAndTypeAndUser(visitRepository.findAllByZone(zone));
    }

    public Visit getVisit(Zone zone, User user, Instant time) {
        return visitRepository.findByZoneAndUserAndTime(zone, user, time).orElse(null);
    }

    public Visit getOrCreate(Zone zone, User user, Instant time, VisitType type) {
        return visitRepository.findByZoneAndUserAndTime(zone, user, time)
                .orElseGet(() -> visitRepository.save(new Visit(zone, user, time, type)));
    }
}
