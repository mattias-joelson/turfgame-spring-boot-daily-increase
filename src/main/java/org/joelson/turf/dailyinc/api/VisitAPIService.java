package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitAPIService {

    @Autowired
    VisitRepository visitRepository;

    public <T> List<T> getSortedVisits(Class<T> type) {
        return visitRepository.findAllSorted(type);
    }

    public <T> List<T> getSortedVisitsByUser(Long userId, Class<T> type) {
        return visitRepository.findAllSortedByUser(userId, type);
    }

    public <T> List<T> getSortedVisitsByZone(Long zoneId, Class<T> type) {
        return visitRepository.findAllSortedByZone(zoneId, type);
    }
}
