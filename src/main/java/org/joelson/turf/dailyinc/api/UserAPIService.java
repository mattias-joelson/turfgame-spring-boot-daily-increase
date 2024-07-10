package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAPIService {

    @Autowired
    UserRepository userRepository;

    public <T> List<T> getSortedUsersBetween(Long minId, Long maxId, Class<T> type) {
        return userRepository.findSortedBetween(minId, maxId, 100, type);
    }

    public <T> List<T> getLastSortedUsers(int count, Class<T> type) {
        return userRepository.findLastSortedReversed(Math.min(count, 100), type).reversed();
    }

    public <T> T getUserById(Long id, Class<T> type) {
        return userRepository.findById(id, type).orElse(null);
    }

    public <T> T getUserByName(String name, Class<T> type) {
        return userRepository.findByName(name, type).orElse(null);
    }
}
