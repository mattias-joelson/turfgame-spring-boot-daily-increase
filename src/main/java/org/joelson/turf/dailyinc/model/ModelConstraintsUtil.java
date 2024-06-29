package org.joelson.turf.dailyinc.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

final class ModelConstraintsUtil {

    private ModelConstraintsUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static Integer isEqualOrAboveZero(Integer i) {
        if (Objects.requireNonNull(i) >= 0) {
            return i;
        }
        throw new IllegalArgumentException(i.toString());
    }

    public static Integer isAboveZero(Integer i) {
        if (Objects.requireNonNull(i) > 0) {
            return i;
        }
        throw new IllegalArgumentException(i.toString());
    }

    public static Integer isEqualOrAbove(Integer newInteger, Integer integer) {
        if (integer == null || newInteger >= integer) {
            return newInteger;
        }
        throw new IllegalArgumentException(newInteger + " is lower than " + integer);
    }

    public static Integer isEqualOrBelow(Integer newInteger, Integer integer) {
        if (integer == null || newInteger <= integer) {
            return newInteger;
        }
        throw new IllegalArgumentException(newInteger + " is higher than " + integer);
    }

    public static Long isAboveZero(Long l) {
        if (Objects.requireNonNull(l) > 0L) {
            return l;
        }
        throw new IllegalArgumentException(l.toString());
    }

    public static Instant isTruncatedToSeconds(Instant instant) {
        if (Objects.requireNonNull(instant).truncatedTo(ChronoUnit.SECONDS).equals(instant)) {
            return instant;
        }
        throw new IllegalArgumentException(instant.toString());
    }

    static Instant isTruncatedToDays(Instant instant) {
        if (Objects.requireNonNull(instant).truncatedTo(ChronoUnit.DAYS).equals(instant)) {
            return instant;
        }
        throw new IllegalArgumentException(instant.toString());
    }

    public static Instant isEqualOrAbove(Instant newTime, Instant time) {
        if (time == null || !newTime.isBefore(time)) {
            return newTime;
        }
        throw new IllegalArgumentException(newTime + " is before " + time);
    }
}
