package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAPIServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserAPIService userAPIService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private static final User USER_ONE = new User(1001L, "UserOne", TIME);
    private static final User USER_TWO = new User(1002L, "UserTwo", TIME);
    private static final User USER_THREE = new User(1003L, "UserThree", TIME);

    private static final List<User> SORTED_USERS_LIST = List.of(USER_ONE, USER_TWO, USER_THREE);

    @Test
    public void testGetSortedUsers() {
        when(userRepository.findAllSorted(User.class)).thenReturn(SORTED_USERS_LIST);

        List<User> sortedUsers = userAPIService.getSortedUsers(User.class);
        assertEquals(SORTED_USERS_LIST, sortedUsers);
        verify(userRepository).findAllSorted(User.class);
    }

    @Test
    public void testGetUserById() {
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
    public void testGetUserByName() {
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
