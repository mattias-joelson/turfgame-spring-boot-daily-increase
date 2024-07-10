package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.ZoneIdAndNameUserIdAndNameVisit;
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
@RequestMapping("/api/visits")
public class VisitController {

    static final String VISITS_RANGE_UNIT = "visits";
    private static final Logger logger = LoggerFactory.getLogger(VisitController.class);

    @Autowired
    VisitAPIService visitAPIService;

    @GetMapping("")
    public ResponseEntity<List<ZoneIdAndNameUserIdAndNameVisit>> getVisits(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace("getVisits()");
        if (range == null) {
            return RangeRequestUtil.handleRequest(VISITS_RANGE_UNIT, ZoneIdAndNameUserIdAndNameVisit.class,
                    visitAPIService::getSortedBetween);
        } else {
            return RangeRequestUtil.handleRequest(VISITS_RANGE_UNIT, range, ZoneIdAndNameUserIdAndNameVisit.class,
                    visitAPIService::getSortedBetween, visitAPIService::getLastSorted);
        }
    }
}
