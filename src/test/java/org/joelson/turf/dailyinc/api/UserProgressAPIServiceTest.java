package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.UserProgressRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
    public void givenUserProgress_whenGetSortedBetween_thenAllReturned() {
        when(userProgressRepository.findSortedBetween(anyInt(), anyInt(), any())).thenReturn(List.of());
        when(userProgressRepository.findSortedBetween(0, SORTED_USER_PROGRESS.size(), UserProgress.class)).thenReturn(SORTED_USER_PROGRESS);

        assertEquals(SORTED_USER_PROGRESS,
                userProgressAPIService.getSortedBetween(0, SORTED_USER_PROGRESS.size() - 1, UserProgress.class));
        verify(userProgressRepository).findSortedBetween(0, SORTED_USER_PROGRESS.size(), UserProgress.class);
    }

    @Test
    public void givenUserProgress_whenGetLastSorted_thenAllReturned() {
        when(userProgressRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(userProgressRepository.findLastSortedReversed(SORTED_USER_PROGRESS.size(), UserProgress.class)).thenReturn(SORTED_USER_PROGRESS.reversed());

        assertEquals(SORTED_USER_PROGRESS,
                userProgressAPIService.getLastSorted(SORTED_USER_PROGRESS.size(), UserProgress.class));
        verify(userProgressRepository).findLastSortedReversed(SORTED_USER_PROGRESS.size(), UserProgress.class);
    }

    @Test
    public void givenUserProgress_whenGetSortedBetweenByUser_thenAllReturned() {
        when(userProgressRepository.findSortedBetweenByUser(anyLong(), anyInt(), anyInt(), any())).thenReturn(List.of());
        when(userProgressRepository.findSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_USER_PROGRESS.size(), UserProgress.class))
                .thenReturn(USER_ONE_SORTED_USER_PROGRESS);
        when(userProgressRepository.findSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_USER_PROGRESS.size(), UserProgress.class))
                .thenReturn(USER_TWO_SORTED_USER_PROGRESS);

        assertEquals(USER_ONE_SORTED_USER_PROGRESS,
                userProgressAPIService.getSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_USER_PROGRESS.size() - 1, UserProgress.class));
        assertEquals(USER_TWO_SORTED_USER_PROGRESS,
                userProgressAPIService.getSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_USER_PROGRESS.size() - 1, UserProgress.class));
        assertEquals(List.of(), userProgressAPIService.getSortedBetweenByUser(1003L, 0, 100, UserProgress.class));
        verify(userProgressRepository, times(3)).findSortedBetweenByUser(anyLong(), anyInt(), anyInt(), any());
    }

    @Test
    public void givenUserProgress_whenGetLastSortedByUser_thenAllReturned() {
        when(userProgressRepository.findLastSortedReversedByUser(anyLong(), anyInt(), any())).thenReturn(List.of());
        when(userProgressRepository.findLastSortedReversedByUser(USER_ONE.getId(), USER_ONE_SORTED_USER_PROGRESS.size(), UserProgress.class))
                .thenReturn(USER_ONE_SORTED_USER_PROGRESS.reversed());
        when(userProgressRepository.findLastSortedReversedByUser(USER_TWO.getId(), USER_TWO_SORTED_USER_PROGRESS.size(), UserProgress.class))
                .thenReturn(USER_TWO_SORTED_USER_PROGRESS.reversed());

        assertEquals(USER_ONE_SORTED_USER_PROGRESS,
                userProgressAPIService.getLastSortedByUser(USER_ONE.getId(), USER_ONE_SORTED_USER_PROGRESS.size(), UserProgress.class));
        assertEquals(USER_TWO_SORTED_USER_PROGRESS,
                userProgressAPIService.getLastSortedByUser(USER_TWO.getId(), USER_TWO_SORTED_USER_PROGRESS.size(), UserProgress.class));
        assertEquals(List.of(), userProgressAPIService.getLastSortedByUser(1003L, 100, UserProgress.class));
        verify(userProgressRepository, times(3)).findLastSortedReversedByUser(anyLong(), anyInt(), any());
    }
}
