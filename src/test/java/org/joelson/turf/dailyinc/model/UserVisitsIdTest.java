package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserVisitsIdTest {

    private static final Long USER = 2L;
    private static final Instant DATE = Instant.now().truncatedTo(ChronoUnit.DAYS);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new UserVisitsId(null, DATE));
        assertThrows(IllegalArgumentException.class, () -> new UserVisitsId(0L, DATE));

        Long user = USER + 3;
        assertNotEquals(USER, user);
        UserVisitsId visitId = new UserVisitsId(user, DATE);
        assertEquals(user, visitId.getUser());

        Long newUser = user + 2;
        assertNotEquals(USER, newUser);
        assertNotEquals(user, newUser);
        visitId.setUser(newUser);
        assertEquals(newUser, visitId.getUser());

        assertThrows(NullPointerException.class, () -> visitId.setUser(null));
        assertThrows(IllegalArgumentException.class, () -> visitId.setUser(0L));
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class, () -> new UserVisitsId(USER, null));
        assertThrows(IllegalArgumentException.class, () -> new UserVisitsId(USER, DATE.plusNanos(3)));

        Instant date = DATE.plus(5, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserVisitsId visitId = new UserVisitsId(USER, date);
        assertEquals(date, visitId.getDate());

        Instant newDate = date.plus(2, ChronoUnit.DAYS);
        assertNotEquals(DATE, newDate);
        assertNotEquals(date, newDate);
        visitId.setDate(newDate);
        assertEquals(newDate, visitId.getDate());

        assertThrows(NullPointerException.class, () -> visitId.setDate(null));
        assertThrows(IllegalArgumentException.class, () -> visitId.setDate(DATE.plusNanos(4)));
    }

    @Test
    public void testEquals() {
        UserVisitsId userVisitsId = new UserVisitsId(USER, DATE);
        assertEquals(userVisitsId, userVisitsId);
        assertNotEquals(userVisitsId, null);
        assertNotEquals(userVisitsId, new UserVisitsId());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(userVisitsId, new UserVisitsId(user, DATE));

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userVisitsId, new UserVisitsId(USER, date));
    }

    @Test
    public void testHashCode() {
        UserVisitsId userVisitsId = new UserVisitsId(USER, DATE);
        assertEquals(userVisitsId.hashCode(), userVisitsId.hashCode());
        assertNotEquals(userVisitsId.hashCode(), new UserVisitsId().hashCode());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(userVisitsId.hashCode(), new UserVisitsId(user, DATE).hashCode());

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userVisitsId.hashCode(), new UserVisitsId(USER, date).hashCode());
    }
}
