package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserProgressIdTest {

    private static final Long USER = 2L;
    private static final UserProgressType TYPE = UserProgressType.DAILY_INCREASE;
    private static final Instant DATE = Instant.now().truncatedTo(ChronoUnit.DAYS);

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new UserProgressId(null, TYPE, DATE));
        assertThrows(IllegalArgumentException.class, () -> new UserProgressId(0L, TYPE, DATE));

        Long user = USER + 3;
        assertNotEquals(USER, user);
        UserProgressId userProgressId = new UserProgressId(user, TYPE, DATE);
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
    public void testType() {
        assertThrows(NullPointerException.class, () -> new UserProgressId(USER, null, DATE));

        UserProgressType type = UserProgressType.DAILY_ADD;
        assertNotEquals(TYPE, type);
        UserProgressId userProgressId = new UserProgressId(USER, type, DATE);
        assertEquals(type, userProgressId.getType());

        UserProgressType newType = UserProgressType.DAILY_FIBONACCI;
        assertNotEquals(TYPE, newType);
        assertNotEquals(type, newType);
        userProgressId.setType(newType);
        assertEquals(newType, userProgressId.getType());

        assertThrows(NullPointerException.class, () -> userProgressId.setType(null));
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class, () -> new UserProgressId(USER, TYPE, null));
        assertThrows(IllegalArgumentException.class, () -> new UserProgressId(USER, TYPE, DATE.plusSeconds(3)));

        Instant date = DATE.plus(5, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserProgressId userProgressId = new UserProgressId(USER, TYPE, date);
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
        UserProgressId userProgressId = new UserProgressId(USER, TYPE, DATE);
        assertEquals(userProgressId, userProgressId);
        assertNotEquals(userProgressId, null);
        assertNotEquals(userProgressId, new UserProgressId());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(userProgressId, new UserProgressId(user, TYPE, DATE));

        UserProgressType type = UserProgressType.DAILY_ADD;
        assertNotEquals(TYPE, type);
        assertNotEquals(userProgressId, new UserProgressId(USER, type, DATE));

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgressId, new UserProgressId(USER, TYPE, date));
    }

    @Test
    public void testHashCode() {
        UserProgressId userProgressId = new UserProgressId(USER, TYPE, DATE);
        assertEquals(userProgressId.hashCode(), userProgressId.hashCode());
        assertNotEquals(userProgressId.hashCode(), new UserProgressId().hashCode());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(userProgressId.hashCode(), new UserProgressId(user, TYPE, DATE).hashCode());

        UserProgressType type = UserProgressType.DAILY_ADD;
        assertNotEquals(TYPE, type);
        assertNotEquals(userProgressId.hashCode(), new UserProgressId(USER, type, DATE).hashCode());

        Instant date = DATE.plus(4, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userProgressId.hashCode(), new UserProgressId(USER, TYPE, date).hashCode());
    }
}
