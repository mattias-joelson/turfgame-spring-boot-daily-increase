package org.joelson.turf.dailyinc.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

final class ControllerUtil {

    private ControllerUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static <T> ResponseEntity<T> respondOk(T object) {
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> respondNotFound() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static Long toLong(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            return (String.valueOf(id).equals(identifier)) ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
