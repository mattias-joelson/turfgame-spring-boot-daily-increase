package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.DailyProgress;
import org.joelson.turf.dailyinc.model.Progress;
import org.joelson.turf.dailyinc.model.ProgressRepository;
import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
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
        int count = 0;
        List<User> allUsersWithVisits = visitService.findDistinctUserOrderById();
        logger.info("Number of users to process: {}", allUsersWithVisits.size());
        for (User user : allUsersWithVisits) {
            calculateProgressForUser(user, count++);
        }
        logger.error("Code missing here!");
    }

    private void calculateProgressForUser(User user, int count) {
        List<Instant> allVisitTimes = visitService.findAllVisitsSortedByTimeForUser(user);
        if (allVisitTimes.isEmpty()) {
            logger.error("No visit times to handle for user {}", user);
            return;
        }

        Progress previousProgress = null;
        int firstVisitTimeIndexOfDate = 0;
        int noDates = 0;
        while (firstVisitTimeIndexOfDate < allVisitTimes.size()) {
            Instant firstVisitTimeOfDate = allVisitTimes.get(firstVisitTimeIndexOfDate);
            Instant date = firstVisitTimeOfDate.truncatedTo(ChronoUnit.DAYS);
            noDates += 1;
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
            previousProgress = progress;

            firstVisitTimeIndexOfDate = firstVisitTimeIndexOfNextDate;
        }
        logger.info("[{}] {} dates for {}", count, noDates, user);
    }
}
