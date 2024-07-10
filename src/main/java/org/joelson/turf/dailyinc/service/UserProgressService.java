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
            if (visits != 1) {
                throw new IllegalArgumentException(String.format("User progress existed for visits=%d > 0.", visits));
            }
            Instant previousDate = date.minus(1, ChronoUnit.DAYS);
            UserProgress previousUserProgress = getUserProgress(user, previousDate);
            if (previousUserProgress == null) {
                userProgressRepository.save(new UserProgress(user, date, visits,
                        new UserProgressTypeProgress(0, 1, time), new UserProgressTypeProgress(0, 1, time),
                        new UserProgressTypeProgress(0, 1, time), new UserProgressTypeProgress(0, 1, time)));
                return 1;
            } else {
                userProgressRepository.save(new UserProgress(user, date, visits,
                        new UserProgressTypeProgress(previousUserProgress.getIncrease().getCompleted(), 1, time),
                        new UserProgressTypeProgress(previousUserProgress.getAdd().getCompleted(), 1, time),
                        new UserProgressTypeProgress(previousUserProgress.getFibonacci().getCompleted(), 2, time),
                        new UserProgressTypeProgress(previousUserProgress.getPowerOfTwo().getCompleted(), 1, time)));
                return 2;
            }
        } else {
            int maxDayCompleted;

            if (userProgress.getVisits() >= visits) {
                throw new IllegalArgumentException(
                        String.format("userProgress.getVisits()=%d >= visits=%d", userProgress.getVisits(), visits));
            }
            userProgress.setVisits(visits);

            maxDayCompleted = Math.abs(increaseUserProgress(userProgress.getIncrease(), visits, time,
                    UserProgressType.DAILY_INCREASE::getNeededVisits));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseUserProgress(userProgress.getAdd(), visits, time,
                            UserProgressType.DAILY_ADD::getNeededVisits)));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseUserProgress(userProgress.getFibonacci(), visits, time,
                            UserProgressType.DAILY_FIBONACCI::getNeededVisits)));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseUserProgress(userProgress.getPowerOfTwo(), visits, time,
                            UserProgressType.DAILY_POWER_OF_TWO::getNeededVisits)));

            userProgressRepository.save(userProgress);
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
        return -completed;
    }
}
