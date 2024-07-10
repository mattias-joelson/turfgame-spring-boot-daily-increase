package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.joelson.turf.dailyinc.model.UserProgressType;
import org.joelson.turf.dailyinc.model.UserProgressTypeProgress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProgressAPIServiceTest {

    @Mock
    UserProgressRepository userProgressRepository;

    @InjectMocks
    UserProgressAPIService userProgressAPIService;

    private static Instant nowWithHour19TruncatedToSeconds() {
        Instant nowTruncatedToSeconds = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ZonedDateTime utcWithHour = nowTruncatedToSeconds.atZone(ZoneId.of("UTC")).withHour(19);
        return utcWithHour.toInstant();
    }

    private static final Instant TIME = nowWithHour19TruncatedToSeconds();
    private static final Instant NEXT_TIME = TIME.plus(1, ChronoUnit.DAYS);

    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Instant NEXT_DATE = DATE.plus(1, ChronoUnit.DAYS);

    private static final User USER_ONE = new User(1001L, "UserOne", NEXT_TIME);
    private static final UserProgress USER_ONE_PROGRESS = new UserProgress(USER_ONE, DATE, 1,
            new UserProgressTypeProgress(0, 1, TIME), new UserProgressTypeProgress(0, 1, TIME),
            new UserProgressTypeProgress(0, 1, TIME), new UserProgressTypeProgress(0, 1, TIME));
    private static final UserProgress USER_ONE_NEXT_PROGRESS = new UserProgress(USER_ONE, NEXT_DATE, 3,
            new UserProgressTypeProgress(1, 2, TIME), new UserProgressTypeProgress(1, 2, TIME),
            new UserProgressTypeProgress(1, 2, TIME), new UserProgressTypeProgress(1, 2, TIME));

    private static final User USER_TWO = new User(1002L, "UserTwo", NEXT_TIME);
    private static final UserProgress USER_TWO_PROGRESS = new UserProgress(USER_TWO, DATE, 10,
            new UserProgressTypeProgress(10, 10, TIME), new UserProgressTypeProgress(4, 4, TIME),
            new UserProgressTypeProgress(6, 6, TIME), new UserProgressTypeProgress(4, 4, TIME));
    private static final UserProgress USER_TWO_NEXT_PROGRESS = new UserProgress(USER_TWO, NEXT_DATE, 13,
            new UserProgressTypeProgress(10, 11, NEXT_TIME), new UserProgressTypeProgress(4, 4, NEXT_TIME),
            new UserProgressTypeProgress(6, 7, NEXT_TIME), new UserProgressTypeProgress(4, 4, NEXT_TIME));

    private static final List<UserProgress> SORTED_USER_PROGRESS = List.of(USER_ONE_PROGRESS, USER_ONE_NEXT_PROGRESS,
            USER_TWO_PROGRESS, USER_TWO_NEXT_PROGRESS);
    private static final List<UserProgress> USER_ONE_SORTED_USER_PROGRESS = List.of(USER_ONE_PROGRESS,
            USER_ONE_NEXT_PROGRESS);
    private static final List<UserProgress> USER_TWO_SORTED_USER_PROGRESS = List.of(USER_TWO_PROGRESS,
            USER_TWO_NEXT_PROGRESS);

    @Test
    public void getSortedUserProgressTest() {
        when(userProgressRepository.findAllSorted(UserProgress.class)).thenReturn(SORTED_USER_PROGRESS);

        List<UserProgress> userProgresses = userProgressAPIService.getSortedUserProgress(UserProgress.class);
        assertEquals(SORTED_USER_PROGRESS, userProgresses);
        verify(userProgressRepository).findAllSorted(UserProgress.class);
    }

    @Test
    public void getSortUserProgressByUserTest() {
        when(userProgressRepository.findAllSortedByUser(anyLong(), eq(UserProgress.class))).thenReturn(List.of());
        when(userProgressRepository.findAllSortedByUser(USER_ONE.getId(), UserProgress.class)).thenReturn(USER_ONE_SORTED_USER_PROGRESS);
        when(userProgressRepository.findAllSortedByUser(USER_TWO.getId(), UserProgress.class)).thenReturn(USER_TWO_SORTED_USER_PROGRESS);

        assertEquals(USER_ONE_SORTED_USER_PROGRESS, userProgressAPIService.getSortedUserProgressByUser(USER_ONE.getId(), UserProgress.class));
        assertEquals(USER_TWO_SORTED_USER_PROGRESS, userProgressAPIService.getSortedUserProgressByUser(USER_TWO.getId(), UserProgress.class));
        assertEquals(List.of(), userProgressAPIService.getSortedUserProgressByUser(1003L, UserProgress.class));
        verify(userProgressRepository,times(3)).findAllSortedByUser(anyLong(), eq(UserProgress.class));
    }
}
