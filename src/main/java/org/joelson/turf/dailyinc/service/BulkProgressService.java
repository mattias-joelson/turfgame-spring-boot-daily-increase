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
        List<Visit> allVisits = visitService.findAllVisitsSortedByTimeForUser(user);
        if (allVisits.isEmpty()) {
            logger.error("No visits to handle for user {}", user);
            return;
        }

        Progress previousProgress = null;
        int firstVisitIndexOfDate = 0;
        int noDates = 0;
        while (firstVisitIndexOfDate < allVisits.size()) {
            Visit firstVisitOfDate = allVisits.get(firstVisitIndexOfDate);
            Instant date = firstVisitOfDate.getTime().truncatedTo(ChronoUnit.DAYS);
            noDates += 1;
            int firstVisitIndexOfNextDate = firstVisitIndexOfDate + 1;
            while (firstVisitIndexOfNextDate < allVisits.size()) {
                Visit lastVisit = allVisits.get(firstVisitIndexOfNextDate);
                if (lastVisit.getTime().truncatedTo(ChronoUnit.DAYS).isAfter(date)) {
                    break;
                }
                firstVisitIndexOfNextDate += 1;
            }
            int visits = firstVisitIndexOfNextDate - firstVisitIndexOfDate;
            Progress progress;
            if (previousProgress != null && previousProgress.getDate().equals(date.minus(1, ChronoUnit.DAYS))) {
                List<Visit> dateVisits = allVisits.subList(firstVisitIndexOfDate, firstVisitIndexOfNextDate);
                DailyProgress incProgress = calcIncreaseDailyProgress(previousProgress.getIncrease(), dateVisits);
                DailyProgress addProgress = calcAddDailyProgress(previousProgress.getAdd(), dateVisits);
                DailyProgress fibProgress = calcFibonacciDailyProgress(previousProgress.getFibonacci(), dateVisits);
                DailyProgress powProgress = calcPowerOfTwoDailyProgress(previousProgress.getPowerOfTwo(), dateVisits);
                progress = new Progress(user, date, visits, incProgress, addProgress, fibProgress, powProgress);
            } else {
                DailyProgress dailyProgress = new DailyProgress(0, 1, allVisits.get(firstVisitIndexOfDate).getTime());
                progress = new Progress(user, date, visits, dailyProgress, dailyProgress, dailyProgress, dailyProgress);
            }
            progressRepository.save(progress);
            previousProgress = progress;

            firstVisitIndexOfDate = firstVisitIndexOfNextDate;
        }
        logger.info("[{}] {} dates for {}", count, noDates, user);
    }
}
