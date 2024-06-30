package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProgressAPIService {

    @Autowired
    UserProgressRepository userProgressRepository;

    public <T> List<T> getSortedUserProgress(Class<T> type) {
        return userProgressRepository.findAllSorted(type);
    }

    public <T> List<T> getSortedUserProgressByUser(Long userId, Class<T> type) {
        return userProgressRepository.findAllSortedByUser(userId, type);
    }
}
