package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.service.UserService;
import org.joelson.turf.dailyinc.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    VisitService visitService;

    @GetMapping("/")
    public List<User> getUsers() {
        logger.trace("getUsers()");
        return userService.getUsers().stream().sorted(Comparator.comparing(User::getId)).toList();
    }

    @GetMapping("/{identifier}")
    public Object getUserByIdentifier(@PathVariable String identifier) {
        logger.trace(String.format("getUserByIdentifier(%s)", identifier));
        User user = lookupUserByIdentifier(identifier);
        if (user != null) {
            return user;
        }
        return new JsonError("/errors/invalid-user-identifier", "Incorrect user identifier", 404, "",
                "/api/users/" + identifier);
    }

    @GetMapping("/{identifier}/visits")
    public Object getZoneVisitsByIdentifier(@PathVariable String identifier) {
        logger.trace(String.format("getZoneVisitsByIdentifier(%s)", identifier));
        User user = lookupUserByIdentifier(identifier);
        if (user != null) {
            return visitService.getSortedVisitsByUser(user);
        }
        return new JsonError("/errors/invalid-zone-identifier", "Incorrect zone identifier", 404, "",
                "/api/users/" + identifier + "/visits");
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
