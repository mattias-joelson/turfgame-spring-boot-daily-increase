package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.UserVisits;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.service.UserProgressService;
import org.joelson.turf.dailyinc.service.UserService;
import org.joelson.turf.dailyinc.service.UserVisitsService;
import org.joelson.turf.dailyinc.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UserService userService;

    @Autowired
    UserProgressService userProgressService;

    @Autowired
    UserVisitsService userVisitsService;

    @Autowired
    VisitService visitService;

    @GetMapping("")
    public List<User> getUsers() {
        logger.trace("getUsers()");
        return userService.getUsers().stream().sorted(Comparator.comparing(User::getId)).toList();
    }

    @GetMapping({ "/", "/{userId}" })
    public ResponseEntity<User> getUserByIdentifier(@PathVariable(required = false) String userId) {
        logger.trace(String.format("getUserByIdentifier(%s)", userId));
        User user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(user);
    }

    @GetMapping({ "//visits", "/{userId}/visits" })
    public ResponseEntity<List<Visit>> getVisitsByIdentifier(@PathVariable(required = false) String userId) {
        logger.trace(String.format("getVisitsByIdentifier(%s)", userId));
        User user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(visitService.getSortedVisitsByUser(user));
    }

    @GetMapping({ "//user-progress", "/{userId}/user-progress" })
    public ResponseEntity<List<UserProgress>> getUserProgressByIdentifier(
            @PathVariable(required = false) String userId) {
        logger.trace(String.format("getUserProgressByIdentifier(%s)", userId));
        User user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(userProgressService.getSortedUserProgressByUser(user));
    }

    @GetMapping({ "//user-visits", "/{userId}/user-visits" })
    public ResponseEntity<List<UserVisits>> getUserVisitsByIdentifier(
            @PathVariable(required = false) String userId) {
        logger.trace(String.format("getUserVisitsByIdentifier(%s)", userId));
        User user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(userVisitsService.getSortedUserVisitsByUser(user));
    }

    private User lookupUserByIdentifier(String identifier) {
        Long id = ControllerUtil.toLong(identifier);
        if (id != null) {
            return userService.getUserById(id);
        } else {
            return userService.getUserByName(identifier);
        }
    }
}
