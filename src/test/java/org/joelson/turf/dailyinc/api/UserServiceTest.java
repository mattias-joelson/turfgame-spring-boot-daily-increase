package org.joelson.turf.dailyinc.service;

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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private static final User USER_ONE = new User(1001L, "UserOne", TIME);
    private static final User USER_TWO = new User(1002L, "UserTwo", TIME);
    private static final User USER_THREE = new User(1003L, "UserThree", TIME);

    private static final List<User> SORTED_USERS_LIST = List.of(USER_ONE, USER_TWO, USER_THREE);

    @Test
    public void testGetSortedUsers() {
        when(userRepository.findAllSorted(User.class)).thenReturn(SORTED_USERS_LIST);

        List<User> sortedUsers = userService.getSortedUsers(User.class);
        assertEquals(SORTED_USERS_LIST, sortedUsers);
        verify(userRepository).findAllSorted(User.class);
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(anyLong(), eq(User.class))).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ONE.getId(), User.class)).thenReturn(Optional.of(USER_ONE));
        when(userRepository.findById(USER_TWO.getId(), User.class)).thenReturn(Optional.of(USER_TWO));
        when(userRepository.findById(USER_THREE.getId(), User.class)).thenReturn(Optional.of(USER_THREE));

        assertNull(userService.getUserById(0L, User.class));
        assertEquals(USER_ONE, userService.getUserById(USER_ONE.getId(), User.class));
        assertEquals(USER_TWO, userService.getUserById(USER_TWO.getId(), User.class));
        assertEquals(USER_THREE, userService.getUserById(USER_THREE.getId(), User.class));
        assertNull(userService.getUserById(4L, User.class));
        assertNull(userService.getUserById(5L, User.class));
        verify(userRepository, times(6)).findById(anyLong(), eq(User.class));
    }

    @Test
    public void testGetUserByName() {
        when(userRepository.findByName(anyString(), eq(User.class))).thenReturn(Optional.empty());
        when(userRepository.findByName(USER_ONE.getName(), User.class)).thenReturn(Optional.of(USER_ONE));
        when(userRepository.findByName(USER_TWO.getName(), User.class)).thenReturn(Optional.of(USER_TWO));
        when(userRepository.findByName(USER_THREE.getName(), User.class)).thenReturn(Optional.of(USER_THREE));

        assertNull(userService.getUserByName("", User.class));
        assertEquals(USER_ONE, userService.getUserByName(USER_ONE.getName(), User.class));
        assertEquals(USER_TWO, userService.getUserByName(USER_TWO.getName(), User.class));
        assertEquals(USER_THREE, userService.getUserByName(USER_THREE.getName(), User.class));
        assertNull(userService.getUserByName(null, User.class));
        assertNull(userService.getUserByName("hej", User.class));
        verify(userRepository).findByName(null, User.class);
        verify(userRepository, times(5)).findByName(anyString(), eq(User.class));
    }

    private static final Long ID = 1001L;
    private static final String NAME = "User";
    private static final String NAME_OTHER = "UserOther";
    private static final Instant TIME_LATER = TIME.plusSeconds(60);

    private static final User USER = new User(ID, NAME, TIME);
    private static final User USER_UPDATED_TIME = new User(ID, NAME, TIME_LATER);
    private static final User USER_UPDATED_NAME_AND_TIME = new User(ID, NAME_OTHER, TIME_LATER);

    private static User copyOf(User user) {
        return new User(user.getId(), user.getName(), user.getTime());
    }

    @Test
    public void givenEmptyRepository_whenGetUpdateOrCreate_thenUserCreated() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User user = userService.getUpdateOrCreate(ID, NAME, TIME);
        assertEquals(USER, user);
        verify(userRepository).findById(ID);
        verify(userRepository).save(USER);
    }

    @Test
    public void givenEqualUser_whenGetUpdateOrCreate_thenUserNotUpdated() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(copyOf(USER)));

        User user = userService.getUpdateOrCreate(ID, NAME, TIME);
        assertEquals(USER, user);
        verify(userRepository).findById(ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void givenOlderUser_whenGetUpdateOrCreate_thenUserNotUpdated() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(copyOf(USER_UPDATED_TIME)));

        User user = userService.getUpdateOrCreate(ID, NAME_OTHER, TIME);
        assertEquals(USER_UPDATED_TIME, user);
        verify(userRepository).findById(ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void givenUpdatedUser_whenGetUpdateOrCreate_thenUserUpdated() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(copyOf(USER)));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User user = userService.getUpdateOrCreate(ID, NAME_OTHER, TIME_LATER);
        assertEquals(USER_UPDATED_NAME_AND_TIME, user);
        verify(userRepository).findById(ID);
        verify(userRepository).save(USER_UPDATED_NAME_AND_TIME);
    }

    @Test
    public void givenLaterUser_whenGetUpdateOrCreate_thenUserUpdated() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(copyOf(USER)));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        User user = userService.getUpdateOrCreate(ID, NAME, TIME_LATER);
        assertEquals(USER_UPDATED_TIME, user);
        verify(userRepository).findById(ID);
        verify(userRepository).save(USER_UPDATED_TIME);
    }
}
