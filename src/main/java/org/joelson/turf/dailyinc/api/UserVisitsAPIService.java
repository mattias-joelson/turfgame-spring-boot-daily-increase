package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.UserVisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserVisitsAPIService {

    @Autowired
    UserVisitsRepository userVisitsRepository;

    public <T> List<T> getSortedUserVisits(Class<T> type) {
        return userVisitsRepository.findAllSorted(type);
    }

    public <T> List<T> getSortedUserVisitsByUser(Long userId, Class<T> type) {
        return userVisitsRepository.findAllSortedByUser(userId, type);
    }
}
