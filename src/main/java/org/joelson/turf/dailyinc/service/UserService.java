package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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
