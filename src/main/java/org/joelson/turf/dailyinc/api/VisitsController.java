package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitsController {

    Logger logger = LoggerFactory.getLogger(VisitsController.class);

    @Autowired
    VisitService visitService;

    private static int compareVisits(Visit o1, Visit o2) {
        int timeDiff = o1.getTime().compareTo(o2.getTime());
        if (timeDiff != 0) {
            return timeDiff;
        }
        int zoneIdDiff = o1.getZone().getId().compareTo(o2.getZone().getId());
        if (zoneIdDiff != 0) {
            return zoneIdDiff;
        }
        if (o1.getType() != o2.getType()) {
            return (o2.getType() == VisitType.ASSIST) ? -1 : 1;
        }
        return o1.getUser().getId().compareTo(o2.getUser().getId());
    }

    @GetMapping("/")
    public List<Visit> getVisits() {
        logger.trace("getVisits()");
        return visitService.getVisits().stream().sorted(VisitsController::compareVisits).toList();
    }
}
