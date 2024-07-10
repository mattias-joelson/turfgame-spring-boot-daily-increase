package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.ProgressId;
import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.joelson.turf.dailyinc.model.DailyProgressType;
import org.joelson.turf.dailyinc.model.DailyProgress;
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
        return userProgressRepository.findById(new ProgressId(user.getId(), date)).orElse(null);
    }

    public int increaseUserProgress(User user, Instant date, Instant time) {
        UserProgress userProgress = getUserProgress(user, date);
        if (userProgress == null) {
            Instant previousDate = date.minus(1, ChronoUnit.DAYS);
            UserProgress previousUserProgress = getUserProgress(user, previousDate);
            if (previousUserProgress == null) {
                userProgressRepository.save(
                        new UserProgress(user, date, 1, new DailyProgress(0, 1, time), new DailyProgress(0, 1, time),
                                new DailyProgress(0, 1, time), new DailyProgress(0, 1, time)));
                return 1;
            } else {
                userProgressRepository.save(new UserProgress(user, date, 1,
                        new DailyProgress(previousUserProgress.getIncrease().getCompleted(), 1, time),
                        new DailyProgress(previousUserProgress.getAdd().getCompleted(), 1, time),
                        new DailyProgress(previousUserProgress.getFibonacci().getCompleted(), 2, time),
                        new DailyProgress(previousUserProgress.getPowerOfTwo().getCompleted(), 1, time)));
                return 2;
            }
        } else {
            int maxDayCompleted;

            int visits = userProgress.getVisits() + 1;
            userProgress.setVisits(visits);

            maxDayCompleted = Math.abs(increaseDailyProgress(userProgress.getIncrease(), visits, time,
                    DailyProgressType.DAILY_INCREASE::getNeededVisits));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseDailyProgress(userProgress.getAdd(), visits, time,
                            DailyProgressType.DAILY_ADD::getNeededVisits)));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseDailyProgress(userProgress.getFibonacci(), visits, time,
                            DailyProgressType.DAILY_FIBONACCI::getNeededVisits)));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseDailyProgress(userProgress.getPowerOfTwo(), visits, time,
                            DailyProgressType.DAILY_POWER_OF_TWO::getNeededVisits)));

            userProgressRepository.save(userProgress);
            return maxDayCompleted;
        }
    }

    private int increaseDailyProgress(
            DailyProgress dailyProgress, int visits, Instant time, Function<Integer, Integer> neededVisits) {
        Integer completed = dailyProgress.getCompleted();
        if (completed <= dailyProgress.getPrevious()) {
            int visitsNeeded = neededVisits.apply(completed + 1);
            if (visits >= visitsNeeded) {
                dailyProgress.setCompleted(completed + 1);
                dailyProgress.setTime(time);
                return completed + 1;
            }
        }
        return -completed;
    }
}
