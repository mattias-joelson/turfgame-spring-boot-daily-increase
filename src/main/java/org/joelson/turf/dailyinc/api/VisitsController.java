package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.Visit;
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

    @GetMapping("/")
    public List<Visit> getVisits() {
        logger.trace("getVisits()");
        return visitService.getSortedVisits();
    }
}
