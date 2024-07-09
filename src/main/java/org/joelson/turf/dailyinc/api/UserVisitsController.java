package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndNameVisits;
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

    private static final Logger logger = LoggerFactory.getLogger(UserVisitsController.class);

    @Autowired
    UserVisitsAPIService userVisitsAPIService;

    @GetMapping("")
    public List<UserIdAndNameVisits> getUserVisits() {
        logger.trace("getUserVisits()");
        return userVisitsAPIService.getSortedUserVisits(UserIdAndNameVisits.class);
    }
}
