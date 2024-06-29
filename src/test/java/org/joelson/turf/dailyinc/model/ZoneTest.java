package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZoneTest {

    private static final Long ID = 1L;
    private static final String NAME = "name";
    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant TIME_WITH_NANOS = TIME.plusNanos(4711);

    @Test
    public void testId() {
        assertThrows(NullPointerException.class, () -> new Zone(null, NAME, TIME));
        assertThrows(IllegalArgumentException.class, () -> new Zone(0L, NAME, TIME));
        assertThrows(IllegalArgumentException.class, () -> new Zone(-3L, NAME, TIME));

        Long id = ID + 1;
        assertNotEquals(ID, id);
        Zone zone = new Zone(id, NAME, TIME);
        assertEquals(id, zone.getId());

    }

    @Test
    public void testName() {
        assertThrows(NullPointerException.class, () -> new Zone(ID, null, TIME));

        String name = "Zone";
        assertNotEquals(NAME, name);
        Zone zone = new Zone(1L, name, TIME);
        assertEquals(name, zone.getName());

        String newName = "";
        assertNotEquals(NAME, newName);
        assertNotEquals(name, newName);
        zone.setName(newName);
        assertEquals(newName, zone.getName());

        assertThrows(NullPointerException.class, () -> zone.setName(null));
    }

    @Test
    public void testTime() {
        assertThrows(NullPointerException.class, () -> new Zone(ID, NAME, null));
        assertThrows(IllegalArgumentException.class, () -> new Zone(ID, NAME, TIME_WITH_NANOS));

        Instant time = TIME.plusSeconds(4);
        assertNotEquals(TIME, time);
        Zone zone = new Zone(ID, NAME, time);
        assertEquals(time, zone.getTime());

        Instant newTime = time.plusSeconds(3);
        assertNotEquals(TIME, newTime);
        assertNotEquals(time, newTime);
        zone.setTime(newTime);
        assertEquals(newTime, zone.getTime());

        assertThrows(NullPointerException.class, () -> zone.setTime(null));
        assertThrows(IllegalArgumentException.class, () -> zone.setTime(newTime.plusNanos(7)));
        assertThrows(IllegalArgumentException.class, () -> zone.setTime(time));
    }

    @Test
    public void testEquals() {
        Zone zone = new Zone(ID, NAME, TIME);
        assertEquals(zone, zone);
        assertNotEquals(zone, null);
        assertNotEquals(zone, new Zone());

        Long id = ID + 1;
        assertNotEquals(ID, id);
        assertNotEquals(zone, new Zone(id, NAME, TIME));

        String name = NAME + NAME;
        assertNotEquals(NAME, name);
        assertNotEquals(zone, new Zone(ID, name, TIME));

        Instant time = TIME.plusSeconds(3);
        assertNotEquals(TIME, time);
        assertNotEquals(zone, new Zone(ID, NAME, time));
    }

    @Test
    public void testHashCode() {
        Zone zone = new Zone(ID, NAME, TIME);
        assertEquals(zone.hashCode(), zone.hashCode());
        assertNotEquals(zone.hashCode(), new Zone().hashCode());

        Long id = ID + 1;
        assertNotEquals(ID.hashCode(), id.hashCode());
        assertNotEquals(zone.hashCode(), new Zone(id, NAME, TIME).hashCode());

        String name = NAME + NAME;
        assertNotEquals(NAME.hashCode(), name.hashCode());
        assertEquals(zone.hashCode(), new Zone(ID, name, TIME).hashCode());

        Instant time = TIME.plusSeconds(3);
        assertNotEquals(TIME.hashCode(), time.hashCode());
        assertEquals(zone.hashCode(), new Zone(ID, NAME, time).hashCode());
    }
}
