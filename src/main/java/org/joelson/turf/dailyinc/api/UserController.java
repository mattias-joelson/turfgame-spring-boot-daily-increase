package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndName;
import org.joelson.turf.dailyinc.projection.UserIdAndNameProgress;
import org.joelson.turf.dailyinc.projection.UserIdAndNameVisits;
import org.joelson.turf.dailyinc.projection.ZoneIdAndNameVisit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String USERS_RANGE_UNIT = "users";
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserAPIService userAPIService;

    @Autowired
    UserProgressAPIService userProgressAPIService;

    @Autowired
    UserVisitsAPIService userVisitsAPIService;

    @Autowired
    VisitAPIService visitAPIService;

    @GetMapping("")
    public ResponseEntity<List<UserIdAndName>> getUsers(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace("getUsers()");
        if (range == null) {
            return RangeRequestUtil.handleIdRequest(USERS_RANGE_UNIT, UserIdAndName.class,
                    userAPIService::getSortedUsersBetween, UserIdAndName::getId);
        } else {
            return RangeRequestUtil.handleIdRequest(USERS_RANGE_UNIT, range, UserIdAndName.class,
                    userAPIService::getSortedUsersBetween, userAPIService::getLastSortedUsers, UserIdAndName::getId);
        }
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
                userProgressAPIService.getSortedUserProgressByUser(user.getId(), UserIdAndNameProgress.class));
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
                userVisitsAPIService.getSortedUserVisitsByUser(user.getId(), UserIdAndNameVisits.class));
    }

    @GetMapping({ "//visits", "/{userId}/visits" })
    public ResponseEntity<List<ZoneIdAndNameVisit>> getVisitsByIdentifier(
            @PathVariable(required = false) String userId) {
        logger.trace(String.format("getVisitsByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        return ControllerUtil.respondOk(visitAPIService.getSortedVisitsByUser(user.getId(), ZoneIdAndNameVisit.class));
    }

    private UserIdAndName lookupUserByIdentifier(String identifier) {
        Long id = ControllerUtil.toLong(identifier);
        if (id != null) {
            return userAPIService.getUserById(id, UserIdAndName.class);
        } else {
            return userAPIService.getUserByName(identifier, UserIdAndName.class);
        }
    }
}
