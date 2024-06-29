package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndNameVisits;
import org.joelson.turf.dailyinc.service.UserVisitsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-visits")
public class UserVisitsController {

    Logger logger = LoggerFactory.getLogger(UserVisitsController.class);

    @Autowired
    UserVisitsService userVisitsService;

    @GetMapping("")
    public List<UserIdAndNameVisits> getUserVisits() {
        logger.trace("getUserVisits()");
        return userVisitsService.getSortedUserVisits(UserIdAndNameVisits.class);
    }
}
