package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.UserProgressId;
import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.joelson.turf.dailyinc.model.UserProgressType;
import org.joelson.turf.dailyinc.model.UserProgressTypeProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Service
public class UserProgressService {

    @Autowired
    UserProgressRepository userProgressRepository;

    private UserProgress getUserProgress(User user, Instant date) {
        return userProgressRepository.findById(new UserProgressId(user.getId(), date)).orElse(null);
    }

    public int increaseUserProgress(User user, Instant date, int visits, Instant time) {
        UserProgress userProgress = getUserProgress(user, date);
        if (userProgress == null) {
            Instant previousDate = date.minus(1, ChronoUnit.DAYS);
            UserProgress previousUserProgress = getUserProgress(user, previousDate);
            if (previousUserProgress == null) {
                userProgressRepository.save(new UserProgress(user, date, new UserProgressTypeProgress(0, 1, time),
                        new UserProgressTypeProgress(0, 1, time), new UserProgressTypeProgress(0, 1, time),
                        new UserProgressTypeProgress(0, 1, time)));
                return 1;
            } else {
                userProgressRepository.save(new UserProgress(user, date,
                        new UserProgressTypeProgress(previousUserProgress.getIncrease().getCompleted(), 1, time),
                        new UserProgressTypeProgress(previousUserProgress.getAdd().getCompleted(), 1, time),
                        new UserProgressTypeProgress(previousUserProgress.getFibonacci().getCompleted(), 2, time),
                        new UserProgressTypeProgress(previousUserProgress.getPowerOfTwo().getCompleted(), 1, time)));
                return 2;
            }
        } else {
            int maxDayCompleted;
            boolean updated = false;

            int increaseCompleted = increaseUserProgress(userProgress.getIncrease(), visits, time,
                    UserProgressType.DAILY_INCREASE::getNeededVisits);
            if (increaseCompleted > 0) {
                updated = true;
                maxDayCompleted = increaseCompleted;
            } else {
                maxDayCompleted = -increaseCompleted;
            }

            int addCompleted = increaseUserProgress(userProgress.getAdd(), visits, time,
                    UserProgressType.DAILY_ADD::getNeededVisits);
            if (addCompleted > 0) {
                updated = true;
                maxDayCompleted = Math.max(maxDayCompleted, addCompleted);
            } else {
                maxDayCompleted = Math.max(maxDayCompleted, -addCompleted);
            }

            int fibonacciCompleted = increaseUserProgress(userProgress.getFibonacci(), visits, time,
                    UserProgressType.DAILY_FIBONACCI::getNeededVisits);
            if (fibonacciCompleted > 0) {
                updated = true;
                maxDayCompleted = Math.max(maxDayCompleted, fibonacciCompleted);
            } else {
                maxDayCompleted = Math.max(maxDayCompleted, -fibonacciCompleted);
            }

            int powerCompleted = increaseUserProgress(userProgress.getPowerOfTwo(), visits, time,
                    UserProgressType.DAILY_POWER_OF_TWO::getNeededVisits);
            if (powerCompleted > 0) {
                updated = true;
                maxDayCompleted = Math.max(maxDayCompleted, powerCompleted);
            } else {
                maxDayCompleted = Math.max(maxDayCompleted, -powerCompleted);
            }

            if (updated) {
                userProgressRepository.save(userProgress);
            }
            return maxDayCompleted;
        }
    }

    private int increaseUserProgress(
            UserProgressTypeProgress userProgressTypeProgress, int visits, Instant time,
            Function<Integer, Integer> neededVisits) {
        Integer completed = userProgressTypeProgress.getCompleted();
        if (completed <= userProgressTypeProgress.getPrevious()) {
            int visitsNeeded = neededVisits.apply(completed + 1);
            if (visits >= visitsNeeded) {
                userProgressTypeProgress.setCompleted(completed + 1);
                userProgressTypeProgress.setTime(time);
                return completed + 1;
            }
        }
        return -userProgressTypeProgress.getCompleted();
    }
}
