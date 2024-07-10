package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProgressAPIService {

    @Autowired
    UserProgressRepository userProgressRepository;

    private static int getNoRows(int firstRow, int lastRow) {
        int size = lastRow - firstRow + 1;
        if (size < 0) {
            size = Integer.MAX_VALUE;
        }
        return Math.min(size, 100);
    }

    public <T> List<T> getSortedBetween(int firstRow, int lastRow, Class<T> type) {
        return userProgressRepository.findSortedBetween(firstRow, getNoRows(firstRow, lastRow), type);
    }

    public <T> List<T> getLastSorted(int rows, Class<T> type) {
        return userProgressRepository.findLastSortedReversed(Math.min(rows, 100), type).reversed();
    }

    public <T> List<T> getSortedBetweenByUser(Long userId, int firstRow, int lastRow, Class<T> type) {
        return userProgressRepository.findSortedBetweenByUser(userId, firstRow, getNoRows(firstRow, lastRow), type);
    }

    public <T> List<T> getLastSortedByUser(Long userId, int rows, Class<T> type) {
        return userProgressRepository.findLastSortedReversedByUser(userId, Math.min(rows, 100), type).reversed();
    }
}
