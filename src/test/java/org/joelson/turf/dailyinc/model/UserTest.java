package org.joelson.turf.dailyinc.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    private static final Long ID = 1L;
    private static final String NAME = "name";
    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant TIME_WITH_NANOS = TIME.plusNanos(4711);

    @Test
    public void testId() {
        assertThrows(NullPointerException.class, () -> new User(null, NAME, TIME));
        assertThrows(IllegalArgumentException.class, () -> new User(0L, NAME, TIME));
        assertThrows(IllegalArgumentException.class, () -> new User(-3L, NAME, TIME));

        Long id = ID + 1;
        assertNotEquals(ID, id);
        User user = new User(id, NAME, TIME);
        assertEquals(id, user.getId());

    }

    @Test
    public void testName() {
        assertThrows(NullPointerException.class, () -> new User(ID, null, TIME));

        String name = "User";
        assertNotEquals(NAME, name);
        User user = new User(1L, name, TIME);
        assertEquals(name, user.getName());

        String newName = "";
        assertNotEquals(NAME, newName);
        assertNotEquals(name, newName);
        user.setName(newName);
        assertEquals(newName, user.getName());

        assertThrows(NullPointerException.class, () -> user.setName(null));
    }

    @Test
    public void testTime() {
        assertThrows(NullPointerException.class, () -> new User(ID, NAME, null));
        assertThrows(IllegalArgumentException.class, () -> new User(ID, NAME, TIME_WITH_NANOS));

        Instant time = TIME.plusSeconds(4);
        assertNotEquals(TIME, time);
        User user = new User(ID, NAME, time);
        assertEquals(time, user.getTime());

        Instant newTime = time.plusSeconds(3);
        assertNotEquals(TIME, newTime);
        assertNotEquals(time, newTime);
        user.setTime(newTime);
        assertEquals(newTime, user.getTime());

        assertThrows(NullPointerException.class, () -> user.setTime(null));
        assertThrows(IllegalArgumentException.class, () -> user.setTime(newTime.plusNanos(7)));
        assertThrows(IllegalArgumentException.class, () -> user.setTime(time));
    }

    @Test
    public void testEquals() {
        User user = new User(ID, NAME, TIME);
        assertEquals(user, user);
        assertNotEquals(user, null);
        assertNotEquals(user, new User());

        Long id = ID + 1;
        assertNotEquals(ID, id);
        assertNotEquals(user, new User(id, NAME, TIME));

        String name = NAME + NAME;
        assertNotEquals(NAME, name);
        assertNotEquals(user, new User(ID, name, TIME));

        Instant time = TIME.plusSeconds(3);
        assertNotEquals(TIME, time);
        assertNotEquals(user, new User(ID, NAME, time));
    }

    @Test
    public void testHashCode() {
        User user = new User(ID, NAME, TIME);
        assertEquals(user.hashCode(), user.hashCode());
        assertNotEquals(user.hashCode(), new User().hashCode());

        Long id = ID + 1;
        assertNotEquals(ID.hashCode(), id.hashCode());
        assertNotEquals(user.hashCode(), new User(id, NAME, TIME).hashCode());

        String name = NAME + NAME;
        assertNotEquals(NAME.hashCode(), name.hashCode());
        assertEquals(user.hashCode(), new User(ID, name, TIME).hashCode());

        Instant time = TIME.plusSeconds(3);
        assertNotEquals(TIME.hashCode(), time.hashCode());
        assertEquals(user.hashCode(), new User(ID, NAME, time).hashCode());
    }
}
