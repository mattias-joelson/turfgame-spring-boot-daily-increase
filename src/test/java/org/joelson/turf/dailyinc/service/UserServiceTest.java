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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

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
