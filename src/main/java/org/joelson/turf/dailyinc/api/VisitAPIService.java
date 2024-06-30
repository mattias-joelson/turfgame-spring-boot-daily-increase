package org.joelson.turf.dailyinc.api;

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
public class VisitAPIService {

    @Autowired
    VisitRepository visitRepository;

    public <T> List<T> getSortedVisits(Class<T> type) {
        return visitRepository.findAllSorted(type);
    }

    public <T,U> List<T> getSortedVisitsByUser(Long userId, Class<T> type) {
        return visitRepository.findAllSortedByUser(userId, type);
    }

    public <T,Z> List<T> getSortedVisitsByZone(Long zoneId, Class<T> type) {
        return visitRepository.findAllSortedByZone(zoneId, type);
    }

    public Visit getVisit(Zone zone, User user, Instant time) {
        return visitRepository.findById(new VisitId(zone.getId(), user.getId(), time)).orElse(null);
    }

    public Visit create(Zone zone, User user, Instant time, VisitType type) {
        return visitRepository.save(new Visit(zone, user, time, type));
    }
}
