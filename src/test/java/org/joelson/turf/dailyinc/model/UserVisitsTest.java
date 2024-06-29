package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserVisitsTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final User USER = new User(1L, "User", TIME);
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Integer VISITS = 3;

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new UserVisits(null, DATE, VISITS));

        User user = new User(USER.getId() * 2, USER.getName(), USER.getTime());
        assertNotEquals(USER, user);
        UserVisits userVisits = new UserVisits(user, DATE, VISITS);
        assertEquals(user, userVisits.getUser());
    }

    @Test
    public void testDate() {
        assertThrows(NullPointerException.class, () -> new UserVisits(USER, null, VISITS));
        assertThrows(IllegalArgumentException.class, () -> new UserVisits(USER, DATE.plusSeconds(3), VISITS));

        Instant date = DATE.plus(3, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        UserVisits userVisits = new UserVisits(USER, date, VISITS);
        assertEquals(date, userVisits.getDate());
    }

    @Test
    public void testVisits() {
        assertThrows(NullPointerException.class, () -> new UserVisits(USER, DATE, null));
        assertThrows(IllegalArgumentException.class, () -> new UserVisits(USER, DATE, 0));
        assertThrows(IllegalArgumentException.class, () -> new UserVisits(USER, DATE, -1));

        Integer visits = VISITS + 3;
        assertNotEquals(VISITS, visits);
        UserVisits userVisits = new UserVisits(USER, DATE, visits);
        assertEquals(visits, userVisits.getVisits());

        Integer newVisits = visits + 1;
        assertNotEquals(VISITS, newVisits);
        assertNotEquals(visits, newVisits);
        userVisits.setVisits(newVisits);
        assertEquals(newVisits, userVisits.getVisits());

        assertThrows(NullPointerException.class, () -> userVisits.setVisits(null));
        assertThrows(IllegalArgumentException.class, () -> userVisits.setVisits(0));
        assertThrows(IllegalArgumentException.class, () -> userVisits.setVisits(visits));
    }

    @Test
    public void testEquals() {
        UserVisits userVisits = new UserVisits(USER, DATE, VISITS);
        assertEquals(userVisits, userVisits);
        assertNotEquals(userVisits, null);
        assertNotEquals(userVisits, new UserVisits());

        User user = new User(USER.getId() + 3, USER.getName(), USER.getTime());
        assertNotEquals(USER, user);
        assertNotEquals(userVisits, new UserVisits(user, DATE, VISITS));

        Instant date = DATE.plus(2, ChronoUnit.DAYS);
        assertNotEquals(DATE, date);
        assertNotEquals(userVisits, new UserVisits(USER, date, VISITS));

        Integer visits = VISITS + 2;
        assertNotEquals(VISITS, visits);
        assertNotEquals(userVisits, new UserVisits(USER, DATE, visits));
    }

    @Test
    public void testHashCode() {
        UserVisits userVisits = new UserVisits(USER, DATE, VISITS);
        assertEquals(userVisits.hashCode(), userVisits.hashCode());
        assertNotEquals(userVisits.hashCode(), new UserVisits().hashCode());

        User user = new User(USER.getId() + 3, USER.getName(), USER.getTime());
        assertNotEquals(USER.hashCode(), user.hashCode());
        assertNotEquals(userVisits.hashCode(), new UserVisits(user, DATE, VISITS).hashCode());

        Instant date = DATE.plus(2, ChronoUnit.DAYS);
        assertNotEquals(DATE.hashCode(), date.hashCode());
        assertNotEquals(userVisits.hashCode(), new UserVisits(USER, date, VISITS).hashCode());

        Integer visits = VISITS + 2;
        assertNotEquals(VISITS.hashCode(), visits.hashCode());
        assertEquals(userVisits.hashCode(), new UserVisits(USER, DATE, visits).hashCode());
    }
}
