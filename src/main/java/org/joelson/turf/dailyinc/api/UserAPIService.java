package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserAPIService {

    @Autowired
    UserRepository userRepository;

    public <T> List<T> getSortedUsers(Class<T> type) {
        return userRepository.findAllSorted(type);
    }

    public <T> T getUserById(Long id, Class<T> type) {
        return userRepository.findById(id, type).orElse(null);
    }

    public <T> T getUserByName(String name, Class<T> type) {
        return userRepository.findByName(name, type).orElse(null);
    }

    public <T> List<T> getSortedUsers(Long minId, Long maxId, Class<T> type) {
        return userRepository.findAllSorted(minId, maxId, 100, type);
    }

    public <T> List<T> getLastSortedUsers(int count, Class<T> type) {
        return userRepository.findLastSorted(Math.min(count, 100), type).reversed();
    }

    public User getUpdateOrCreate(Long id, String name, Instant time) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return userRepository.save(new User(id, name, time));
        } else if (time.isAfter(user.getTime())) {
            if (!user.getName().equals(name)) {
                user.setName(name);
            }
            user.setTime(time);
            return userRepository.save(user);
        }
        return user;
    }
}
