package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Progress;
import org.joelson.turf.dailyinc.model.ProgressId;
import org.joelson.turf.dailyinc.model.ProgressRepository;
import org.joelson.turf.dailyinc.model.DailyProgressType;
import org.joelson.turf.dailyinc.model.DailyProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Service
public class IncrementalProgressService {

    @Autowired
    ProgressRepository progressRepository;

    private Progress getProgress(User user, Instant date) {
        return progressRepository.findById(new ProgressId(user.getId(), date)).orElse(null);
    }

    public int increaseProgress(User user, Instant date, Instant time) {
        Progress progress = getProgress(user, date);
        if (progress == null) {
            Instant previousDate = date.minus(1, ChronoUnit.DAYS);
            Progress previousProgress = getProgress(user, previousDate);
            if (previousProgress == null) {
                progressRepository.save(
                        new Progress(user, date, 1, new DailyProgress(0, 1, time), new DailyProgress(0, 1, time),
                                new DailyProgress(0, 1, time), new DailyProgress(0, 1, time)));
                return 1;
            } else {
                progressRepository.save(new Progress(user, date, 1,
                        new DailyProgress(previousProgress.getIncrease().getCompleted(), 1, time),
                        new DailyProgress(previousProgress.getAdd().getCompleted(), 1, time),
                        new DailyProgress(previousProgress.getFibonacci().getCompleted(), 2, time),
                        new DailyProgress(previousProgress.getPowerOfTwo().getCompleted(), 1, time)));
                return 2;
            }
        } else {
            int maxDayCompleted;

            int visits = progress.getVisits() + 1;
            progress.setVisits(visits);

            maxDayCompleted = Math.abs(increaseDailyProgress(progress.getIncrease(), visits, time,
                    DailyProgressType.DAILY_INCREASE::getNeededVisits));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(increaseDailyProgress(progress.getAdd(), visits, time,
                    DailyProgressType.DAILY_ADD::getNeededVisits)));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseDailyProgress(progress.getFibonacci(), visits, time,
                            DailyProgressType.DAILY_FIBONACCI::getNeededVisits)));

            maxDayCompleted = Math.max(maxDayCompleted, Math.abs(
                    increaseDailyProgress(progress.getPowerOfTwo(), visits, time,
                            DailyProgressType.DAILY_POWER_OF_TWO::getNeededVisits)));

            progressRepository.save(progress);
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
