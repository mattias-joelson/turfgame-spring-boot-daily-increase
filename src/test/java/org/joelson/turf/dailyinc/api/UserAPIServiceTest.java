package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserRepository;
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
public class UserAPIServiceTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final User USER_ONE = new User(1001L, "UserOne", TIME);
    private static final User USER_TWO = new User(1002L, "UserTwo", TIME);
    private static final User USER_THREE = new User(1003L, "UserThree", TIME);

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserAPIService userAPIService;

    private static User createUser(Long id) {
        return new User(id, "User" + id, TIME);
    }

    private static List<User> createList(long minId, long maxId, long stepId, int limit, Predicate<Integer> sizeBeing) {
        List<User> users = ListTestUtil.createList(minId, maxId, stepId, UserAPIServiceTest::createUser);
        return limitList(limit, sizeBeing, users);
    }

    private static List<User> createReversedList(
            long maxId, long minId, long stepId, int limit, Predicate<Integer> sizeBeing) {
        List<User> users = ListTestUtil.createReversedList(maxId, minId, stepId, UserAPIServiceTest::createUser);
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
        when(userRepository.findSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());
        when(userRepository.findSortedBetween(minId, maxId, limit, User.class)).thenReturn(
                createList(minId, maxId, stepId, limit, size -> size < limit));

        List<User> users = userAPIService.getSortedUsersBetween(minId, maxId, User.class);
        verifyUserList(users, minId, maxId, stepId, users.size(), users.size());
        verify(userRepository).findSortedBetween(minId, maxId, limit, User.class);
    }

    @Test
    public void givenMoreUsersInRangeThanLimit_whenFindAllSortedBetween_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 3001L;
        long stepId = 10L;
        int limit = 100;
        when(userRepository.findSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());
        when(userRepository.findSortedBetween(minId, maxId, limit, User.class)).thenReturn(
                createList(minId, maxId, stepId, limit, size -> size == limit));

        List<User> users = userAPIService.getSortedUsersBetween(minId, maxId, User.class);
        verifyUserList(users, minId, maxId, stepId, limit, limit);
        verify(userRepository).findSortedBetween(minId, maxId, limit, User.class);
    }

    @Test
    public void givenUsersOutsideOfRange_whenFindAllSortedBetween_thenNoneReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        when(userRepository.findSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());

        List<User> users = userAPIService.getSortedUsersBetween(minId, maxId, User.class);
        assertTrue(users.isEmpty());
        verify(userRepository).findSortedBetween(minId, maxId, 100, User.class);
    }

    @Test
    public void givenFewUsers_whenFindLastSortedReversed_thenAllReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 100L;
        int count = 20;
        int limit = 100;
        when(userRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(userRepository.findLastSortedReversed(count, User.class)).thenReturn(
                createReversedList(maxId, minId, stepId, limit, size -> size < limit));

        List<User> users = userAPIService.getLastSortedUsers(count, User.class);
        verifyUserList(users, minId, maxId, stepId, users.size(), count);
        verify(userRepository).findLastSortedReversed(count, User.class);
    }

    @Test
    public void givenManyUsers_whenFindLastSortedReversed_thenLimitReturned() {
        long maxId = 2001L;
        long stepId = 10L;
        int limit = 100;
        int count = 120;
        when(userRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(userRepository.findLastSortedReversed(limit, User.class)).thenReturn(
                createReversedList(maxId, 1001L, stepId, limit, size -> size == limit));

        List<User> users = userAPIService.getLastSortedUsers(count, User.class);
        verifyUserList(users, users.getFirst().getId(), maxId, stepId, limit, limit);
        verify(userRepository).findLastSortedReversed(limit, User.class);
    }

    @Test
    public void givenNoUsers_whenFindLastSortedReversed_thenNoneReturned() {
        when(userRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());

        List<User> users = userAPIService.getLastSortedUsers(300, User.class);
        assertTrue(users.isEmpty());
        verify(userRepository).findLastSortedReversed(100, User.class);
    }

    @Test
    public void givenUsers_whenGetUserById_thenExistingReturned() {
        when(userRepository.findById(anyLong(), eq(User.class))).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ONE.getId(), User.class)).thenReturn(Optional.of(USER_ONE));
        when(userRepository.findById(USER_TWO.getId(), User.class)).thenReturn(Optional.of(USER_TWO));
        when(userRepository.findById(USER_THREE.getId(), User.class)).thenReturn(Optional.of(USER_THREE));

        assertNull(userAPIService.getUserById(0L, User.class));
        assertEquals(USER_ONE, userAPIService.getUserById(USER_ONE.getId(), User.class));
        assertEquals(USER_TWO, userAPIService.getUserById(USER_TWO.getId(), User.class));
        assertEquals(USER_THREE, userAPIService.getUserById(USER_THREE.getId(), User.class));
        assertNull(userAPIService.getUserById(4L, User.class));
        assertNull(userAPIService.getUserById(5L, User.class));
        verify(userRepository, times(6)).findById(anyLong(), eq(User.class));
    }

    @Test
    public void givenUsers_whenGetUserByName_thenExistingReturned() {
        when(userRepository.findByName(anyString(), eq(User.class))).thenReturn(Optional.empty());
        when(userRepository.findByName(USER_ONE.getName(), User.class)).thenReturn(Optional.of(USER_ONE));
        when(userRepository.findByName(USER_TWO.getName(), User.class)).thenReturn(Optional.of(USER_TWO));
        when(userRepository.findByName(USER_THREE.getName(), User.class)).thenReturn(Optional.of(USER_THREE));

        assertNull(userAPIService.getUserByName("", User.class));
        assertEquals(USER_ONE, userAPIService.getUserByName(USER_ONE.getName(), User.class));
        assertEquals(USER_TWO, userAPIService.getUserByName(USER_TWO.getName(), User.class));
        assertEquals(USER_THREE, userAPIService.getUserByName(USER_THREE.getName(), User.class));
        assertNull(userAPIService.getUserByName(null, User.class));
        assertNull(userAPIService.getUserByName("hej", User.class));
        verify(userRepository).findByName(null, User.class);
        verify(userRepository, times(5)).findByName(anyString(), eq(User.class));
    }
}
