package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndNameProgress;
import org.joelson.turf.dailyinc.service.UserProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-progress")
public class UserProgressController {

    private final Logger logger = LoggerFactory.getLogger(UserProgressController.class);

    @Autowired
    UserProgressService userProgressService;

    @GetMapping("")
    public List<UserIdAndNameProgress> getUserProgress() {
        logger.trace("getUserProgress()");
        return userProgressService.getSortedUserProgress(UserIdAndNameProgress.class);
    }

}
