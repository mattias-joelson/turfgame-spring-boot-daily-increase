package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndName;
import org.joelson.turf.dailyinc.projection.UserIdAndNameProgress;
import org.joelson.turf.dailyinc.projection.UserIdAndNameVisits;
import org.joelson.turf.dailyinc.projection.ZoneIdAndNameVisit;
import org.joelson.turf.dailyinc.service.UserProgressService;
import org.joelson.turf.dailyinc.service.UserService;
import org.joelson.turf.dailyinc.service.UserVisitsService;
import org.joelson.turf.dailyinc.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UserService userService;

    @Autowired
    UserProgressService userProgressService;

    @Autowired
    UserVisitsService userVisitsService;

    @Autowired
    VisitService visitService;

//    @GetMapping("")
//    public List<UserIdAndName> getUsers() {
//        logger.trace("getUsers()");
//        return userService.getSortedUsers(UserIdAndName.class);
//    }

    @GetMapping("")
    public ResponseEntity<List<UserIdAndName>> getUsers(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace("getUsers()");
        logger.info("Range: " + range);
        Long minId = null;
        Long maxId = null;
        if (range != null) {
            if (range.startsWith("users=")) {
                int hyphenIndex = range.indexOf('-');
                if (hyphenIndex >= 0) {
                    String minIdString = range.substring(6, hyphenIndex);
                    minId = ControllerUtil.toLong(minIdString);
                    String maxIdString = range.substring(hyphenIndex + 1);
                    maxId = ControllerUtil.toLong(maxIdString);
                }
            }
            if ((minId == null && maxId == null) || (minId != null && maxId != null && minId.compareTo(maxId) > 0)) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.ACCEPT_RANGES, "users");
                return new ResponseEntity<>(httpHeaders, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }
        }
        logger.info("minId=" + minId + ", maxId=" + maxId);
        List<UserIdAndName> users;
        if (minId != null) {
            users = userService.getSortedUsers(minId, Objects.requireNonNullElse(maxId, Long.MAX_VALUE), UserIdAndName.class);
        } else if (maxId != null) {
            users = userService.getLastSortedUsers(maxId.intValue(), UserIdAndName.class);
        } else {
            users = userService.getSortedUsers(0L, Long.MAX_VALUE, UserIdAndName.class);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, "users");
        if (users.isEmpty()) {
            return new ResponseEntity<>(httpHeaders, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        }
        httpHeaders.add(HttpHeaders.CONTENT_RANGE,
                String.format("users %d-%d/*", users.getFirst().getId(), users.getLast().getId()));
        return new ResponseEntity<>(users, httpHeaders, (range == null) ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping({ "/", "/{userId}" })
    public ResponseEntity<UserIdAndName> getUserByIdentifier(@PathVariable(required = false) String userId) {
        logger.trace(String.format("getUserByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(user);
    }

    @GetMapping({ "//user-progress", "/{userId}/user-progress" })
    public ResponseEntity<List<UserIdAndNameProgress>> getUserProgressByIdentifier(
            @PathVariable(required = false) String userId) {
        logger.trace(String.format("getUserProgressByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(
                userProgressService.getSortedUserProgressByUser(user.getId(), UserIdAndNameProgress.class));
    }

    @GetMapping({ "//user-visits", "/{userId}/user-visits" })
    public ResponseEntity<List<UserIdAndNameVisits>> getUserVisitsByIdentifier(
            @PathVariable(required = false) String userId) {
        logger.trace(String.format("getUserVisitsByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(
                userVisitsService.getSortedUserVisitsByUser(user.getId(), UserIdAndNameVisits.class));
    }

    @GetMapping({ "//visits", "/{userId}/visits" })
    public ResponseEntity<List<ZoneIdAndNameVisit>> getVisitsByIdentifier(
            @PathVariable(required = false) String userId) {
        logger.trace(String.format("getVisitsByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(visitService.getSortedVisitsByUser(user.getId(), ZoneIdAndNameVisit.class));
    }

    private UserIdAndName lookupUserByIdentifier(String identifier) {
        Long id = ControllerUtil.toLong(identifier);
        if (id != null) {
            return userService.getUserById(id, UserIdAndName.class);
        } else {
            return userService.getUserByName(identifier, UserIdAndName.class);
        }
    }
}
