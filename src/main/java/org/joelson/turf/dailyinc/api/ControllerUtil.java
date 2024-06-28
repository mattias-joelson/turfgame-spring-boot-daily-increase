package org.joelson.turf.dailyinc.api;

final class ControllerUtil {

    private ControllerUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
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
