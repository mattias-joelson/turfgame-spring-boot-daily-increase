package org.joelson.turf.dailyinc.api;

//import org.hibernate.query.spi.Limit;
import org.springframework.data.domain.Limit;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitAPIService {

    //private Logger logger = LoggerFactory.getLogger(VisitAPIService.class);

    @Autowired
    VisitRepository visitRepository;

    /*public <T> List<T> getSortedVisitsBetween(int firstRow, int lastRow, Class<T> type) {
        logger.info(String.format("getSortedVisitsBetween(%d, %d, %s)", firstRow, lastRow, type));
        if (firstRow <= 0) {
            throw new IllegalArgumentException(String.format("firstRow=%d <= 0", firstRow));
        }
        if (lastRow < firstRow) {
            throw new IllegalArgumentException(String.format("firstRow=%d > lastRow=%d", firstRow, lastRow));
        }
        int size = lastRow - firstRow + 1;
        logger.info("size=" + size);
        int min = Math.min(size, 100);
        logger.info("min=" + min);
        //Limit limit = new Limit(firstRow, min);
        //logger.info("limit=" + limit + " (" + limit.getFirstRow() + ", " + limit.getFirstRowJpa() + ", " + limit.getMaxRows() + ", " + limit.getMaxRowsJpa() + ")");
        //List<T> allSorted = visitRepository.findAllSortedLimit(min, type);
        //List<Visit> allSorted = visitRepository.findByOrderByTimeAsc(Limit.of(1));
        List<T> allSorted = visitRepository.findAllSorted(Limit.of(min), type);
        logger.info("allSorted.size()=" + allSorted.size());
        logger.info("allSorted.getFirst()=" + allSorted.getFirst());
        logger.info("allSorted.getLast()=" + allSorted.getLast());
        return allSorted;
        //return List.of();
    }

    public <T> List<T> getAllSortedLimit(int limit, Class<T> type) {
        int actualLimit = Math.min(20, limit);
        logger.info(String.format("limit=%d, actualLimit=%d", limit, actualLimit));
        List<T> allSorted = visitRepository.findAllSortedLimit(actualLimit, type);
        logger.info("allSorted.size()=" + allSorted.size());
        logger.info("allSorted.getFirst()=" + allSorted.getFirst());
        logger.info("allSorted.getLast()=" + allSorted.getLast());
        return allSorted;
    }*/

    public <T> List<T> getSortedVisitsBetween(Integer firstRow, Integer lastRow, Class<T> type) {
        //logger.info(String.format("getSortedVisitsBetween(%d, %d, %s)", firstRow, lastRow, type));
        if (firstRow < 0) {
            throw new IllegalArgumentException(String.format("firstRow=%d < 0", firstRow));
        }
        if (lastRow < firstRow) {
            throw new IllegalArgumentException(String.format("firstRow=%d > lastRow=%d", firstRow, lastRow));
        }
        int size = lastRow - firstRow + 1;
        //logger.info("size=" + size);
        int minSize = Math.min(size, 100);
        //logger.info("minSize=" + minSize);
        //List<T> allSorted = visitRepository.findAllSorted(Limit.of(minSize), type);
        List<T> allSorted = visitRepository.findAllSorted(firstRow, minSize, type);
        //logger.info("allSorted.size()=" + allSorted.size());
        //logger.info("allSorted.getFirst()=" + allSorted.getFirst());
        //logger.info("allSorted.getLast()=" + allSorted.getLast());
        return allSorted;
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
