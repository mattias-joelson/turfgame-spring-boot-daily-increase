package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.service.UserService;
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

    private static Long toLong(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            return (String.valueOf(id).equals(identifier)) ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/")
    public List<User> getUsers() {
        logger.trace("getUsers()");
        return userService.getUsers().stream().sorted(Comparator.comparing(User::getId)).toList();
    }

    @GetMapping("/{identifier}")
    public Object getUserByIdentifier(@PathVariable String identifier) {
        logger.trace(String.format("getUserByIdentifier(%s)", identifier));
        User user;
        Long id = toLong(identifier);
        if (id != null) {
            user = userService.getUserById(id);
        } else {
            user = userService.getUserByName(identifier);
        }
        if (user != null) {
            return user;
        }
        return new JsonError("/errors/invalid-user-identifier", "Incorrect user identifier", 404, "",
                "/api/users/" + identifier);
    }
}
