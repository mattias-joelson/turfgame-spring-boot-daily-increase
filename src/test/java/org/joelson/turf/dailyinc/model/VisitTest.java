package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VisitTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Zone ZONE = new Zone(1L, "Zone", TIME);
    private static final User USER = new User(1L, "User", TIME);
    private static final VisitType TYPE = VisitType.TAKEOVER;

    @Test
    public void testZone() {
        assertThrows(NullPointerException.class, () -> new Visit(null, USER, TIME, TYPE));

        Zone zone = new Zone(ZONE.getId() * 2, ZONE.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(ZONE, zone);
        Visit visit = new Visit(zone, USER, TIME, TYPE);
        assertEquals(zone, visit.getZone());
    }

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new Visit(ZONE, null, TIME, TYPE));

        User user = new User(USER.getId() * 2, USER.getName() + "Name", TIME.plusSeconds(3));
        assertNotEquals(USER, user);
        Visit visit = new Visit(ZONE, user, TIME, TYPE);
        assertEquals(user, visit.getUser());
    }

    @Test
    public void testTime() {
        assertThrows(NullPointerException.class, () -> new Visit(ZONE, USER, null, TYPE));
        assertThrows(IllegalArgumentException.class, () -> new Visit(ZONE, USER, TIME.plusNanos(3), TYPE));

        Instant time = TIME.plusSeconds(5);
        assertNotEquals(TIME, time);
        Visit visit = new Visit(ZONE, USER, time, TYPE);
        assertEquals(time, visit.getTime());
    }

    @Test
    public void testType() {
        assertThrows(NullPointerException.class, () -> new Visit(ZONE, USER, TIME, null));

        VisitType type = VisitType.REVISIT;
        assertNotEquals(type, TYPE);
        Visit visit = new Visit(ZONE, USER, TIME, type);
        assertEquals(type, visit.getType());
    }

    @Test
    public void testEquals() {
        Visit visit = new Visit(ZONE, USER, TIME, TYPE);
        assertEquals(visit, visit);
        assertNotEquals(visit, null);
        assertNotEquals(visit, new Visit());

        Zone zone = new Zone(ZONE.getId() + 1, ZONE.getName(), ZONE.getTime());
        assertNotEquals(ZONE, zone);
        assertNotEquals(visit, new Visit(zone, USER, TIME, TYPE));

        User user = new User(USER.getId() + 1, USER.getName(), USER.getTime());
        assertNotEquals(USER, user);
        assertNotEquals(visit, new Visit(ZONE, user, TIME, TYPE));

        Instant time = TIME.plusSeconds(4);
        assertNotEquals(TIME, time);
        assertNotEquals(visit, new Visit(ZONE, USER, time, TYPE));

        VisitType type = VisitType.REVISIT;
        assertNotEquals(TYPE, type);
        assertNotEquals(visit, new Visit(ZONE, USER, TIME, type));
    }

    @Test
    public void testHashCode() {
        Visit visit = new Visit(ZONE, USER, TIME, TYPE);
        assertEquals(visit.hashCode(), visit.hashCode());
        assertNotEquals(visit.hashCode(), new Visit().hashCode());

        Zone zone = new Zone(ZONE.getId() + 1, ZONE.getName(), ZONE.getTime());
        assertNotEquals(ZONE, zone);
        assertNotEquals(visit.hashCode(), new Visit(zone, USER, TIME, TYPE).hashCode());

        User user = new User(USER.getId() + 1, USER.getName(), USER.getTime());
        assertNotEquals(USER, user);
        assertNotEquals(visit.hashCode(), new Visit(ZONE, user, TIME, TYPE).hashCode());

        Instant time = TIME.plusSeconds(4);
        assertNotEquals(TIME, time);
        assertNotEquals(visit.hashCode(), new Visit(ZONE, USER, time, TYPE).hashCode());

        VisitType type = VisitType.REVISIT;
        assertNotEquals(TYPE, type);
        assertEquals(visit.hashCode(), new Visit(ZONE, USER, TIME, type).hashCode());
    }
}
