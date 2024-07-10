package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Progress;
import org.joelson.turf.dailyinc.model.ProgressRepository;
import org.joelson.turf.dailyinc.model.DailyProgress;
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
public class ProgressAPIServiceTest {

    @Mock
    ProgressRepository progressRepository;

    @InjectMocks
    ProgressAPIService progressAPIService;

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
    private static final Progress USER_ONE_PROGRESS = new Progress(USER_ONE, DATE, 1, new DailyProgress(0, 1, TIME),
            new DailyProgress(0, 1, TIME), new DailyProgress(0, 1, TIME), new DailyProgress(0, 1, TIME));
    private static final Progress USER_ONE_NEXT_PROGRESS = new Progress(USER_ONE, NEXT_DATE, 3,
            new DailyProgress(1, 2, TIME), new DailyProgress(1, 2, TIME), new DailyProgress(1, 2, TIME),
            new DailyProgress(1, 2, TIME));

    private static final User USER_TWO = new User(1002L, "UserTwo", NEXT_TIME);
    private static final Progress USER_TWO_PROGRESS = new Progress(USER_TWO, DATE, 10, new DailyProgress(10, 10, TIME),
            new DailyProgress(4, 4, TIME), new DailyProgress(6, 6, TIME), new DailyProgress(4, 4, TIME));
    private static final Progress USER_TWO_NEXT_PROGRESS = new Progress(USER_TWO, NEXT_DATE, 13,
            new DailyProgress(10, 11, NEXT_TIME), new DailyProgress(4, 4, NEXT_TIME),
            new DailyProgress(6, 7, NEXT_TIME), new DailyProgress(4, 4, NEXT_TIME));

    private static final List<Progress> SORTED_PROGRESS = List.of(USER_ONE_PROGRESS, USER_ONE_NEXT_PROGRESS,
            USER_TWO_PROGRESS, USER_TWO_NEXT_PROGRESS);
    private static final List<Progress> USER_ONE_SORTED_PROGRESS = List.of(USER_ONE_PROGRESS, USER_ONE_NEXT_PROGRESS);
    private static final List<Progress> USER_TWO_SORTED_PROGRESS = List.of(USER_TWO_PROGRESS, USER_TWO_NEXT_PROGRESS);

    @Test
    public void givenUserProgress_whenGetSortedBetween_thenAllReturned() {
        when(progressRepository.findSortedBetween(anyInt(), anyInt(), any())).thenReturn(List.of());
        when(progressRepository.findSortedBetween(0, SORTED_PROGRESS.size(), Progress.class))
                .thenReturn(SORTED_PROGRESS);

        assertEquals(SORTED_PROGRESS,
                progressAPIService.getSortedBetween(0, SORTED_PROGRESS.size() - 1, Progress.class));
        verify(progressRepository).findSortedBetween(0, SORTED_PROGRESS.size(), Progress.class);
    }

    @Test
    public void givenUserProgress_whenGetLastSorted_thenAllReturned() {
        when(progressRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(progressRepository.findLastSortedReversed(SORTED_PROGRESS.size(), Progress.class))
                .thenReturn(SORTED_PROGRESS.reversed());

        assertEquals(SORTED_PROGRESS, progressAPIService.getLastSorted(SORTED_PROGRESS.size(), Progress.class));
        verify(progressRepository).findLastSortedReversed(SORTED_PROGRESS.size(), Progress.class);
    }

    @Test
    public void givenUserProgress_whenGetSortedBetweenByUser_thenAllReturned() {
        when(progressRepository.findSortedBetweenByUser(anyLong(), anyInt(), anyInt(), any())).thenReturn(List.of());
        when(progressRepository.findSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_PROGRESS.size(), Progress.class))
                .thenReturn(USER_ONE_SORTED_PROGRESS);
        when(progressRepository.findSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_PROGRESS.size(), Progress.class))
                .thenReturn(USER_TWO_SORTED_PROGRESS);

        assertEquals(USER_ONE_SORTED_PROGRESS,
                progressAPIService.getSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_PROGRESS.size() - 1, Progress.class));
        assertEquals(USER_TWO_SORTED_PROGRESS,
                progressAPIService.getSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_PROGRESS.size() - 1, Progress.class));
        assertEquals(List.of(), progressAPIService.getSortedBetweenByUser(1003L, 0, 100, Progress.class));
        verify(progressRepository, times(3)).findSortedBetweenByUser(anyLong(), anyInt(), anyInt(), any());
    }

    @Test
    public void givenUserProgress_whenGetLastSortedByUser_thenAllReturned() {
        when(progressRepository.findLastSortedReversedByUser(anyLong(), anyInt(), any())).thenReturn(List.of());
        when(progressRepository.findLastSortedReversedByUser(USER_ONE.getId(), USER_ONE_SORTED_PROGRESS.size(), Progress.class))
                .thenReturn(USER_ONE_SORTED_PROGRESS.reversed());
        when(progressRepository.findLastSortedReversedByUser(USER_TWO.getId(), USER_TWO_SORTED_PROGRESS.size(), Progress.class))
                .thenReturn(USER_TWO_SORTED_PROGRESS.reversed());

        assertEquals(USER_ONE_SORTED_PROGRESS,
                progressAPIService.getLastSortedByUser(USER_ONE.getId(), USER_ONE_SORTED_PROGRESS.size(), Progress.class));
        assertEquals(USER_TWO_SORTED_PROGRESS,
                progressAPIService.getLastSortedByUser(USER_TWO.getId(), USER_TWO_SORTED_PROGRESS.size(), Progress.class));
        assertEquals(List.of(), progressAPIService.getLastSortedByUser(1003L, 100, Progress.class));
        verify(progressRepository, times(3)).findLastSortedReversedByUser(anyLong(), anyInt(), any());
    }
}
