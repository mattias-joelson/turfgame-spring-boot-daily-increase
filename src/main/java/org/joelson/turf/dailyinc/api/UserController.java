package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.projection.UserIdAndName;
import org.joelson.turf.dailyinc.projection.UserProgress;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserAPIService userAPIService;

    @Autowired
    ProgressAPIService progressAPIService;

    @Autowired
    VisitAPIService visitAPIService;

    @GetMapping("")
    public ResponseEntity<List<UserIdAndName>> getUsers(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace("getUsers()");
        if (range == null) {
            return RangeRequestUtil.handleIdRequest(USERS_RANGE_UNIT, UserIdAndName.class,
                    userAPIService::getSortedBetween, UserIdAndName::getId);
        } else {
            return RangeRequestUtil.handleIdRequest(USERS_RANGE_UNIT, range, UserIdAndName.class,
                    userAPIService::getSortedBetween, userAPIService::getLastSorted, UserIdAndName::getId);
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
    public ResponseEntity<List<UserProgress>> getUserProgressByIdentifier(
            @PathVariable(required = false) String userId,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace(String.format("getUserProgressByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        if (range == null) {
            return RangeRequestUtil.handleRequest(ProgressController.PROGRESS_RANGE_UNIT, UserProgress.class,
                    (firstRow, lastRow, type) -> progressAPIService.getSortedBetweenByUser(user.getId(), firstRow, lastRow, type));
        } else {
            return RangeRequestUtil.handleRequest(ProgressController.PROGRESS_RANGE_UNIT, range, UserProgress.class,
                    (firstRow, lastRow, type) -> progressAPIService.getSortedBetweenByUser(user.getId(), firstRow, lastRow, type),
                    (rows, type) -> progressAPIService.getLastSortedByUser(user.getId(), rows, type));
        }
    }

    @GetMapping({ "//visits", "/{userId}/visits" })
    public ResponseEntity<List<ZoneIdAndNameVisit>> getVisitsByIdentifier(
            @PathVariable(required = false) String userId,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        logger.trace(String.format("getVisitsByIdentifier(%s)", userId));
        UserIdAndName user = lookupUserByIdentifier(userId);
        if (user == null) {
            return ControllerUtil.respondNotFound();
        }
        if (range == null) {
            return RangeRequestUtil.handleRequest(VisitController.VISITS_RANGE_UNIT, ZoneIdAndNameVisit.class,
                    (firstRow, lastRow, type) -> visitAPIService.getSortedBetweenByUser(user.getId(), firstRow, lastRow, type));
        } else {
            return RangeRequestUtil.handleRequest(VisitController.VISITS_RANGE_UNIT, range, ZoneIdAndNameVisit.class,
                    (firstRow, lastRow, type) -> visitAPIService.getSortedBetweenByUser(user.getId(), firstRow, lastRow, type),
                    (rows, type) -> visitAPIService.getLastSortedByUser(user.getId(), rows, type));
        }
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
