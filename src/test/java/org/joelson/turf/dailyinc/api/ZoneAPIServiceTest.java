package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZoneAPIServiceTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);
    private static final Zone ZONE_THREE = new Zone(3L, "ZoneThree", TIME);
    @Mock
    ZoneRepository zoneRepository;
    @InjectMocks
    ZoneAPIService zoneAPIService;

    private static User createUser(Long id) {
        return new User(id, "User" + id, TIME);
    }

    private static List<User> createList(long minId, long maxId, long stepId, int limit, Predicate<Integer> sizeBeing) {
        List<User> users = ListTestUtil.createList(minId, maxId, stepId, ZoneAPIServiceTest::createUser);
        return limitList(limit, sizeBeing, users);
    }

    private static List<User> createReversedList(
            long maxId, long minId, long stepId, int limit, Predicate<Integer> sizeBeing) {
        List<User> users = ListTestUtil.createReversedList(maxId, minId, stepId, ZoneAPIServiceTest::createUser);
        return limitList(limit, sizeBeing, users);
    }

    private static List<User> limitList(int limit, Predicate<Integer> sizeBeing, List<User> users) {
        if (users.size() > limit) {
            users = users.subList(0, limit);
        }
        if (sizeBeing != null) {
            assertSize(users, sizeBeing);
        }
        return Collections.unmodifiableList(users);
    }

    private static void assertSize(List<User> users, Predicate<Integer> sizeBeing) {
        assertTrue(sizeBeing.test(users.size()), () -> String.format("users.size()=%d", users.size()));
    }

    private static void verifyUserList(
            List<User> users, long minId, long maxId, long stepId, int minSize, int maxSize) {
        ListTestUtil.verifyList(users, minId, maxId, stepId, minSize, maxSize, User::getId);
    }

    @Test
    public void givenFewUsersInRange_whenFindAllSortedBetween_thenAllReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        long stepId = 100L;
        int limit = 100;
        when(zoneRepository.findAllSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findAllSortedBetween(minId, maxId, limit, User.class)).thenReturn(
                createList(minId, maxId, stepId, limit, size -> size < limit));

        List<User> users = zoneAPIService.getSortedUsersBetween(minId, maxId, User.class);
        verifyUserList(users, minId, maxId, stepId, users.size(), users.size());
        verify(zoneRepository).findAllSortedBetween(minId, maxId, limit, User.class);
    }

    @Test
    public void givenMoreUsersInRangeThanLimit_whenFindAllSortedBetween_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 3001L;
        long stepId = 10L;
        int limit = 100;
        when(zoneRepository.findAllSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findAllSortedBetween(minId, maxId, limit, User.class)).thenReturn(
                createList(minId, maxId, stepId, limit, size -> size == limit));

        List<User> users = zoneAPIService.getSortedUsersBetween(minId, maxId, User.class);
        verifyUserList(users, minId, maxId, stepId, limit, limit);
        verify(zoneRepository).findAllSortedBetween(minId, maxId, limit, User.class);
    }

    @Test
    public void givenUsersOutsideOfRange_whenFindAllSortedBetween_thenNoneReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        when(zoneRepository.findAllSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());

        List<User> users = zoneAPIService.getSortedUsersBetween(minId, maxId, User.class);
        assertTrue(users.isEmpty());
        verify(zoneRepository).findAllSortedBetween(minId, maxId, 100, User.class);
    }

    @Test
    public void givenFewUsers_whenFindLastSortedReversed_thenAllReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 100L;
        int count = 20;
        int limit = 100;
        when(zoneRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findLastSortedReversed(count, User.class)).thenReturn(
                createReversedList(maxId, minId, stepId, limit, size -> size < limit));

        List<User> users = zoneAPIService.getLastSortedUsers(count, User.class);
        verifyUserList(users, minId, maxId, stepId, users.size(), count);
        verify(zoneRepository).findLastSortedReversed(count, User.class);
    }

    @Test
    public void givenManyUsers_whenFindLastSortedReversed_thenLimitReturned() {
        long maxId = 2001L;
        long stepId = 10L;
        int limit = 100;
        int count = 120;
        when(zoneRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findLastSortedReversed(limit, User.class)).thenReturn(
                createReversedList(maxId, 1001L, stepId, limit, size -> size == limit));

        List<User> users = zoneAPIService.getLastSortedUsers(count, User.class);
        verifyUserList(users, users.getFirst().getId(), maxId, stepId, limit, limit);
        verify(zoneRepository).findLastSortedReversed(limit, User.class);
    }

    @Test
    public void givenNoUsers_whenFindLastSortedReversed_thenNoneReturned() {
        when(zoneRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());

        List<User> users = zoneAPIService.getLastSortedUsers(300, User.class);
        assertTrue(users.isEmpty());
        verify(zoneRepository).findLastSortedReversed(100, User.class);
    }

    @Test
    public void givenZones_whenGetZoneById_thenExistingReturned() {
        when(zoneRepository.findById(anyLong(), eq(Zone.class))).thenReturn(Optional.empty());
        when(zoneRepository.findById(ZONE_ONE.getId(), Zone.class)).thenReturn(Optional.of(ZONE_ONE));
        when(zoneRepository.findById(ZONE_TWO.getId(), Zone.class)).thenReturn(Optional.of(ZONE_TWO));
        when(zoneRepository.findById(ZONE_THREE.getId(), Zone.class)).thenReturn(Optional.of(ZONE_THREE));

        assertNull(zoneAPIService.getZoneById(0L, Zone.class));
        assertEquals(ZONE_ONE, zoneAPIService.getZoneById(ZONE_ONE.getId(), Zone.class));
        assertEquals(ZONE_TWO, zoneAPIService.getZoneById(ZONE_TWO.getId(), Zone.class));
        assertEquals(ZONE_THREE, zoneAPIService.getZoneById(ZONE_THREE.getId(), Zone.class));
        assertNull(zoneAPIService.getZoneById(4L, Zone.class));
        assertNull(zoneAPIService.getZoneById(5L, Zone.class));
        verify(zoneRepository, times(6)).findById(anyLong(), eq(Zone.class));
    }

    @Test
    public void givenZones_whenGetUserByName_thenExistingReturned() {
        when(zoneRepository.findByName(anyString(), eq(Zone.class))).thenReturn(Optional.empty());
        when(zoneRepository.findByName(ZONE_ONE.getName(), Zone.class)).thenReturn(Optional.of(ZONE_ONE));
        when(zoneRepository.findByName(ZONE_TWO.getName(), Zone.class)).thenReturn(Optional.of(ZONE_TWO));
        when(zoneRepository.findByName(ZONE_THREE.getName(), Zone.class)).thenReturn(Optional.of(ZONE_THREE));

        assertNull(zoneAPIService.getZoneByName("", Zone.class));
        assertEquals(ZONE_ONE, zoneAPIService.getZoneByName(ZONE_ONE.getName(), Zone.class));
        assertEquals(ZONE_TWO, zoneAPIService.getZoneByName(ZONE_TWO.getName(), Zone.class));
        assertEquals(ZONE_THREE, zoneAPIService.getZoneByName(ZONE_THREE.getName(), Zone.class));
        assertNull(zoneAPIService.getZoneByName(null, Zone.class));
        assertNull(zoneAPIService.getZoneByName("hej", Zone.class));
        verify(zoneRepository).findByName(null, Zone.class);
        verify(zoneRepository, times(5)).findByName(anyString(), eq(Zone.class));
    }
}
