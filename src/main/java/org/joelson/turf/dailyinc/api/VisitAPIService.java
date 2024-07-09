package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitAPIService {

    @Autowired
    VisitRepository visitRepository;

    public <T> List<T> getSortedVisitsBetween(Integer firstRow, Integer lastRow, Class<T> type) {
        int size = lastRow - firstRow + 1;
        if (size < 0) {
            size = Integer.MAX_VALUE;
        }
        int minSize = Math.min(size, 100);
        return visitRepository.findAllSorted(firstRow, minSize, type);
    }

    public <T> List<T> getLastSortedVisits(int rows, Class<T> type) {
        return visitRepository.findAllSortedReversed(rows, type).reversed();
    }

    public <T> List<T> getSortedVisitsByUser(Long userId, Class<T> type) {
        return visitRepository.findAllSortedByUser(userId, type);
    }

    public <T> List<T> getSortedVisitsByZone(Long zoneId, Class<T> type) {
        return visitRepository.findAllSortedByZone(zoneId, type);
    }
}
