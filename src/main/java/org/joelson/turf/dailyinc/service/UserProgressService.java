package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.UserProgressId;
import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.joelson.turf.dailyinc.model.UserProgressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserProgressService {

    @Autowired
    UserProgressRepository userProgressRepository;

    public UserProgress getUserProgress(User user, UserProgressType type, Instant date) {
        return userProgressRepository.findById(new UserProgressId(user.getId(), type, date)).orElse(null);
    }

    public int increaseUserProgress(User user, Instant date, int visits, Instant time) {
        int maxDayCompleted = -1;
        for (UserProgressType type : UserProgressType.values()) {
            int dayCompleted = increaseUserProgress(user, type, date, visits, time);
            if (dayCompleted > maxDayCompleted) {
                maxDayCompleted = dayCompleted;
            }
        }
        return maxDayCompleted;
    }

    private int increaseUserProgress(User user, UserProgressType type, Instant date, int visits, Instant time) {
        UserProgress userProgress = getUserProgress(user, type, date);
        if (userProgress == null) {
            Instant previousDate = date.minus(1, ChronoUnit.DAYS);
            UserProgress previousUserProgress = getUserProgress(user, type, previousDate);
            if (previousUserProgress == null) {
                userProgressRepository.save(new UserProgress(user, type, date, 0, 1, time));
                return 1;
            } else if (type == UserProgressType.DAILY_FIBONACCI) {
                userProgressRepository.save(
                        new UserProgress(user, type, date, previousUserProgress.getDayCompleted(), 2, time));
                return 2;
            } else {
                userProgressRepository.save(
                        new UserProgress(user, type, date, previousUserProgress.getDayCompleted(), 1, time));
                return 1;
            }
        } else {
            Integer dayCompleted = userProgress.getDayCompleted();
            if (dayCompleted <= userProgress.getPreviousDayCompleted()) {
                int visitsNeeded = type.getNeededVisits(dayCompleted + 1);
                if (visits >= visitsNeeded) {
                    userProgress.setDayCompleted(dayCompleted + 1);
                    userProgress.setTimeCompleted(time);
                    userProgressRepository.save(userProgress);
                }
            }
            return userProgress.getDayCompleted();
        }
    }
}
