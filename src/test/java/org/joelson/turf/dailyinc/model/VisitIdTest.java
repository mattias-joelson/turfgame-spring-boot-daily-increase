package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VisitIdTest {

    private static final Long ZONE = 1L;
    private static final Long USER = 2L;
    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    public void testZone() {
        assertThrows(NullPointerException.class, () -> new VisitId(null, USER, TIME));
        assertThrows(IllegalArgumentException.class, () -> new VisitId(0L, USER, TIME));

        Long zone = ZONE + 4;
        assertNotEquals(ZONE, zone);
        VisitId visitId = new VisitId(zone, USER, TIME);
        assertEquals(zone, visitId.getZone());

        Long newZone = zone + 3;
        assertNotEquals(ZONE, newZone);
        assertNotEquals(zone, newZone);
        visitId.setZone(newZone);
        assertEquals(newZone, visitId.getZone());

        assertThrows(NullPointerException.class, () -> visitId.setZone(null));
        assertThrows(IllegalArgumentException.class, () -> visitId.setZone(0L));
    }

    @Test
    public void testUser() {
        assertThrows(NullPointerException.class, () -> new VisitId(ZONE, null, TIME));
        assertThrows(IllegalArgumentException.class, () -> new VisitId(ZONE, 0L, TIME));

        Long user = USER + 3;
        assertNotEquals(USER, user);
        VisitId visitId = new VisitId(ZONE, user, TIME);
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
    public void testTime() {
        assertThrows(NullPointerException.class, () -> new VisitId(ZONE, USER, null));
        assertThrows(IllegalArgumentException.class, () -> new VisitId(ZONE, USER, TIME.plusNanos(3)));

        Instant time = TIME.plusSeconds(5);
        assertNotEquals(TIME, time);
        VisitId visitId = new VisitId(ZONE, USER, time);
        assertEquals(time, visitId.getTime());

        Instant newTime = time.plusSeconds(2);
        assertNotEquals(TIME, newTime);
        assertNotEquals(time, newTime);
        visitId.setTime(newTime);
        assertEquals(newTime, visitId.getTime());

        assertThrows(NullPointerException.class, () -> visitId.setTime(null));
        assertThrows(IllegalArgumentException.class, () -> visitId.setTime(TIME.plusNanos(4)));
    }

    @Test
    public void testEquals() {
        VisitId visitId = new VisitId(ZONE, USER, TIME);
        assertEquals(visitId, visitId);
        assertNotEquals(visitId, null);
        assertNotEquals(visitId, new Visit());

        Long zone = ZONE + 14;
        assertNotEquals(ZONE, zone);
        assertNotEquals(visitId, new VisitId(zone, USER, TIME));

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(visitId, new VisitId(ZONE, user, TIME));

        Instant time = TIME.plusSeconds(4);
        assertNotEquals(TIME, time);
        assertNotEquals(visitId, new VisitId(ZONE, USER, time));
    }

    @Test
    public void testHashCode() {
        VisitId visitId = new VisitId(ZONE, USER, TIME);
        assertEquals(visitId.hashCode(), visitId.hashCode());
        assertNotEquals(visitId.hashCode(), new Visit().hashCode());

        Long zone = ZONE + 14;
        assertNotEquals(ZONE, zone);
        assertNotEquals(visitId.hashCode(), new VisitId(zone, USER, TIME).hashCode());

        Long user = USER + 7;
        assertNotEquals(USER, user);
        assertNotEquals(visitId.hashCode(), new VisitId(ZONE, user, TIME).hashCode());

        Instant time = TIME.plusSeconds(4);
        assertNotEquals(TIME, time);
        assertNotEquals(visitId.hashCode(), new VisitId(ZONE, USER, time).hashCode());
    }
}
