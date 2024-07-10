package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndNameProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-progress")
public class ProgressController {

    public static final String PROGRESS_RANGE_UNIT = "progress";
    private static final Logger logger = LoggerFactory.getLogger(ProgressController.class);

    @Autowired
    ProgressAPIService progressAPIService;

    @GetMapping("")
    public ResponseEntity<List<UserIdAndNameProgress>> getUserProgress(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace("getUserProgress()");
        if (range == null) {
            return RangeRequestUtil.handleRequest(PROGRESS_RANGE_UNIT, UserIdAndNameProgress.class,
                    progressAPIService::getSortedBetween);
        } else {
            return RangeRequestUtil.handleRequest(PROGRESS_RANGE_UNIT, range, UserIdAndNameProgress.class,
                    progressAPIService::getSortedBetween, progressAPIService::getLastSorted);
        }
    }

}
