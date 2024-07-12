package org.joelson.turf.dailyinc.service;

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

@Service
public class BulkProgressService {

    // for each distinct(user_id) from visits
    // for each distinct(date(time)) as date_time, count(1) as vis from visits where user_id = :user_id order by
    // date_time, group by date_time;
    // select time from visits where user_id = :user_id and date(time) = :date_time order by time;

    // create first
    // create new
    //   improve on previous if possible

    // binary-search on visits

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
//            logger.info("    {} {} visits from {} to {} exclusive (size={})", date,
//                    (firstVisitIndexOfNextDate - firstVisitIndexOfDate), firstVisitIndexOfDate,
//                    firstVisitIndexOfNextDate, allVisits.size());
            firstVisitIndexOfDate = firstVisitIndexOfNextDate;
        }
        logger.info("[{}] {} dates for {}", count, noDates, user);
    }
}
