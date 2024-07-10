package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProgressIdTest {

    private static final Long USER = 2L;
    private static final Instant DATE = Instant.now().truncatedTo(ChronoUnit.DAYS);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new ProgressId(null, DATE));
        assertThrows(IllegalArgumentException.class, () -> new ProgressId(0L, DATE));

        Long user = USER + 3;
        assertNotEquals(USER, user);
        ProgressId progressId = new ProgressId(user, DATE);
        assertEquals(user, progressId.getUser());

        Long newUser = user + 2;
        assertNotEquals(USER, newUser);
        assertNotEquals(user, newUser);
        progressId.setUser(newUser);
        assertEquals(newUser, progressId.getUser());

        assertThrows(NullPointerException.class, () -> progressId.setUser(null));
        assertThrows(IllegalArgumentException.class, () -> progressId.setUser(0L));
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class, () -> new ProgressId(USER, null));
        assertThrows(IllegalArgumentException.class, () -> new ProgressId(USER, DATE.plusSeconds(3)));

        Instant date = DATE.plus(5, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        ProgressId progressId = new ProgressId(USER, date);
        assertEquals(date, progressId.getDate());

        Instant newDate = date.plus(2, ChronoUnit.DAYS);
        assertNotEquals(DATE, newDate);
        assertNotEquals(date, newDate);
        progressId.setDate(newDate);
        assertEquals(newDate, progressId.getDate());

        assertThrows(NullPointerException.class, () -> progressId.setDate(null));
        assertThrows(IllegalArgumentException.class, () -> progressId.setDate(DATE.plusSeconds(4)));
    }

    @Test
    public void testEquals() {
        ProgressId progressId = new ProgressId(USER, DATE);
        assertEquals(progressId, progressId);
        assertNotEquals(progressId, null);
        assertNotEquals(progressId, new ProgressId());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(progressId, new ProgressId(user, DATE));

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(progressId, new ProgressId(USER, date));
    }

    @Test
    public void testHashCode() {
        ProgressId progressId = new ProgressId(USER, DATE);
        assertEquals(progressId.hashCode(), progressId.hashCode());
        assertNotEquals(progressId.hashCode(), new ProgressId().hashCode());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(progressId.hashCode(), new ProgressId(user, DATE).hashCode());

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(progressId.hashCode(), new ProgressId(USER, date).hashCode());
    }
}
