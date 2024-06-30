package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.ZoneIdAndNameUserIdAndNameVisit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final Logger logger = LoggerFactory.getLogger(VisitController.class);

    @Autowired
    VisitAPIService visitAPIService;

    @GetMapping("")
    public List<ZoneIdAndNameUserIdAndNameVisit> getVisits() {
        logger.trace("getVisits()");
        return visitAPIService.getSortedVisits(ZoneIdAndNameUserIdAndNameVisit.class);
    }
}
