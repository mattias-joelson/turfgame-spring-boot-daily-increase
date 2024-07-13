package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.DailyProgress;
import org.joelson.turf.dailyinc.model.Progress;
import org.joelson.turf.dailyinc.model.ProgressRepository;
import org.joelson.turf.dailyinc.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcAddDailyProgress;
import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcFibonacciDailyProgress;
import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcIncreaseDailyProgress;
import static org.joelson.turf.dailyinc.model.DailyProgressVisitsCache.calcPowerOfTwoDailyProgress;

@Service
public class BulkProgressService {

    private static final Logger logger = LoggerFactory.getLogger(BulkProgressService.class);

    @Autowired
    ProgressRepository progressRepository;

    @Autowired
    VisitService visitService;

    public void calculateProgress() {
        if (!progressRepository.isEmpty()) {
            logger.error("ProgressRepository is not empty!");
        }
        List<User> allUsersWithVisits = visitService.findDistinctUserOrderById();
        logger.info("Number of users to process: {}", allUsersWithVisits.size());
        Instant nextLogInstant = Instant.now().plusSeconds(30);
        int handledUsers = 0;
        int handledDates = 0;
        for (User user : allUsersWithVisits) {
            if (nextLogInstant.isBefore(Instant.now())) {
                logger.info("Handled {} users, {} progress dates.", handledUsers, handledDates);
                nextLogInstant = nextLogInstant.plusSeconds(30);
            }
            handledDates += calculateProgressForUser(user);
            handledUsers += 1;
        }
        logger.info("Done, handled {} users, {} progress dates.", handledUsers, handledDates);
    }

    int calculateProgressForUser(User user) {
        List<Instant> allVisitTimes = visitService.findAllSortedVisitTimesByUser(user);
        if (allVisitTimes.isEmpty()) {
            throw new IllegalArgumentException("No visit times to handle for user " + user);
        }

        Progress previousProgress = null;
        int firstVisitTimeIndexOfDate = 0;
        int handledDates = 0;
        while (firstVisitTimeIndexOfDate < allVisitTimes.size()) {
            Instant firstVisitTimeOfDate = allVisitTimes.get(firstVisitTimeIndexOfDate);
            Instant date = firstVisitTimeOfDate.truncatedTo(ChronoUnit.DAYS);
            int firstVisitTimeIndexOfNextDate = firstVisitTimeIndexOfDate + 1;
            while (firstVisitTimeIndexOfNextDate < allVisitTimes.size()) {
                Instant lastVisitTime = allVisitTimes.get(firstVisitTimeIndexOfNextDate);
                if (lastVisitTime.truncatedTo(ChronoUnit.DAYS).isAfter(date)) {
                    break;
                }
                firstVisitTimeIndexOfNextDate += 1;
            }
            int visits = firstVisitTimeIndexOfNextDate - firstVisitTimeIndexOfDate;
            Progress progress;
            if (previousProgress != null && previousProgress.getDate().equals(date.minus(1, ChronoUnit.DAYS))) {
                List<Instant> dateVisitTimes = allVisitTimes.subList(firstVisitTimeIndexOfDate, firstVisitTimeIndexOfNextDate);
                DailyProgress incProgress = calcIncreaseDailyProgress(previousProgress.getIncrease(), dateVisitTimes);
                DailyProgress addProgress = calcAddDailyProgress(previousProgress.getAdd(), dateVisitTimes);
                DailyProgress fibProgress = calcFibonacciDailyProgress(previousProgress.getFibonacci(), dateVisitTimes);
                DailyProgress powProgress = calcPowerOfTwoDailyProgress(previousProgress.getPowerOfTwo(), dateVisitTimes);
                progress = new Progress(user, date, visits, incProgress, addProgress, fibProgress, powProgress);
            } else {
                DailyProgress dailyProgress = new DailyProgress(0, 1, allVisitTimes.get(firstVisitTimeIndexOfDate));
                progress = new Progress(user, date, visits, dailyProgress, dailyProgress, dailyProgress, dailyProgress);
            }
            progressRepository.save(progress);
            handledDates += 1;
            previousProgress = progress;

            firstVisitTimeIndexOfDate = firstVisitTimeIndexOfNextDate;
        }
        return handledDates;
    }
}
