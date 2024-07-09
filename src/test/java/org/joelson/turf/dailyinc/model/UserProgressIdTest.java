package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProgressIdTest {

    private static final Long USER = 2L;
    private static final Instant DATE = Instant.now().truncatedTo(ChronoUnit.DAYS);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new UserProgressId(null, DATE));
        assertThrows(IllegalArgumentException.class, () -> new UserProgressId(0L, DATE));

        Long user = USER + 3;
        assertNotEquals(USER, user);
        UserProgressId userProgressId = new UserProgressId(user, DATE);
        assertEquals(user, userProgressId.getUser());

        Long newUser = user + 2;
        assertNotEquals(USER, newUser);
        assertNotEquals(user, newUser);
        userProgressId.setUser(newUser);
        assertEquals(newUser, userProgressId.getUser());

        assertThrows(NullPointerException.class, () -> userProgressId.setUser(null));
        assertThrows(IllegalArgumentException.class, () -> userProgressId.setUser(0L));
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class, () -> new UserProgressId(USER, null));
        assertThrows(IllegalArgumentException.class, () -> new UserProgressId(USER, DATE.plusSeconds(3)));

        Instant date = DATE.plus(5, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserProgressId userProgressId = new UserProgressId(USER, date);
        assertEquals(date, userProgressId.getDate());

        Instant newDate = date.plus(2, ChronoUnit.DAYS);
        assertNotEquals(DATE, newDate);
        assertNotEquals(date, newDate);
        userProgressId.setDate(newDate);
        assertEquals(newDate, userProgressId.getDate());

        assertThrows(NullPointerException.class, () -> userProgressId.setDate(null));
        assertThrows(IllegalArgumentException.class, () -> userProgressId.setDate(DATE.plusSeconds(4)));
    }

    @Test
    public void testEquals() {
        UserProgressId userProgressId = new UserProgressId(USER, DATE);
        assertEquals(userProgressId, userProgressId);
        assertNotEquals(userProgressId, null);
        assertNotEquals(userProgressId, new UserProgressId());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(userProgressId, new UserProgressId(user, DATE));

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgressId, new UserProgressId(USER, date));
    }

    @Test
    public void testHashCode() {
        UserProgressId userProgressId = new UserProgressId(USER, DATE);
        assertEquals(userProgressId.hashCode(), userProgressId.hashCode());
        assertNotEquals(userProgressId.hashCode(), new UserProgressId().hashCode());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(userProgressId.hashCode(), new UserProgressId(user, DATE).hashCode());

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgressId.hashCode(), new UserProgressId(USER, date).hashCode());
    }
}
