package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Progress;
import org.joelson.turf.dailyinc.model.ProgressId;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IncrementalProgressServiceTest {

    @Mock
    ProgressRepository progressRepository;

    @InjectMocks
    IncrementalProgressService incrementalProgressService;

    private static Instant nowWithHour19TruncatedToSeconds() {
        Instant nowTruncatedToSeconds = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ZonedDateTime utcWithHour = nowTruncatedToSeconds.atZone(ZoneId.of("UTC")).withHour(19);
        return utcWithHour.toInstant();
    }

    private static final Instant TIME = nowWithHour19TruncatedToSeconds();
    private static final Instant NEXT_TIME = TIME.plus(1, ChronoUnit.DAYS);

    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Instant NEXT_DATE = DATE.plus(1, ChronoUnit.DAYS);

    private static final User USER = new User(1001L, "User", NEXT_TIME);

    private static final ProgressId PROGRESS_ID = new ProgressId(USER.getId(), DATE);

    private static final Progress PROGRESS = new Progress(USER, DATE, 1, new DailyProgress(0, 1, TIME),
            new DailyProgress(0, 1, TIME), new DailyProgress(0, 1, TIME), new DailyProgress(0, 1, TIME));

    private static final ProgressId NEXT_PROGRESS_ID = new ProgressId(USER.getId(), NEXT_DATE);

    private static final Progress NEXT_PROGRESS = new Progress(USER, NEXT_DATE, 1, new DailyProgress(1, 1, NEXT_TIME),
            new DailyProgress(1, 1, NEXT_TIME), new DailyProgress(1, 2, NEXT_TIME), new DailyProgress(1, 1, NEXT_TIME));

    private static final Instant LATER_TIME = NEXT_TIME.plusSeconds(93);

    private static final Progress LATER_PROGRESS = new Progress(USER, NEXT_DATE, 2, new DailyProgress(1, 2, LATER_TIME),
            new DailyProgress(1, 1, NEXT_TIME), new DailyProgress(1, 2, NEXT_TIME),
            new DailyProgress(1, 2, LATER_TIME));

    private static final Instant EVEN_LATER_TIME = LATER_TIME.plusSeconds(129);

    private static final Progress EVEN_LATER_PROGRESS = new Progress(USER, NEXT_DATE, 3,
            new DailyProgress(1, 2, LATER_TIME), new DailyProgress(1, 2, EVEN_LATER_TIME),
            new DailyProgress(1, 2, NEXT_TIME), new DailyProgress(1, 2, LATER_TIME));

    private static Progress copyOf(Progress that) {
        return new Progress(that.getUser(), that.getDate(), that.getVisits(), copyOf(that.getIncrease()),
                copyOf(that.getAdd()), copyOf(that.getFibonacci()), copyOf(that.getPowerOfTwo()));
    }

    private static DailyProgress copyOf(DailyProgress that) {
        return new DailyProgress(that.getPrevious(), that.getCompleted(), that.getTime());
    }

    @Test
    public void givenEmptyRepository_whenIncreaseProgress_thenProgressCreated() {
        when(progressRepository.findById(any(ProgressId.class))).thenReturn(Optional.empty());

        int maxDayCompleted = incrementalProgressService.increaseProgress(USER, DATE, TIME);
        assertEquals(PROGRESS.getIncrease().getCompleted(), maxDayCompleted);
        verify(progressRepository).findById(PROGRESS_ID);
        verify(progressRepository).save(PROGRESS);
    }

    @Test
    public void givenDateProgress_whenIncreaseProgress_thenProgressNotUpdated() {
        when(progressRepository.findById(any(ProgressId.class))).thenReturn(Optional.empty());
        when(progressRepository.findById(PROGRESS_ID)).thenReturn(Optional.of(copyOf(PROGRESS)));

        int maxDayCompleted = incrementalProgressService.increaseProgress(USER, DATE, TIME.plusSeconds(60));
        assertEquals(PROGRESS.getIncrease().getCompleted(), maxDayCompleted);
        verify(progressRepository).findById(PROGRESS_ID);
        verify(progressRepository).save(any(Progress.class));
    }

    @Test
    public void givenDateProgressButNoNextDateProgress_whenIncreaseProgress_thenProgressCreated() {
        when(progressRepository.findById(any(ProgressId.class))).thenReturn(Optional.empty());
        when(progressRepository.findById(PROGRESS_ID)).thenReturn(Optional.of(copyOf(PROGRESS)));

        int maxDayCompleted = incrementalProgressService.increaseProgress(USER, NEXT_DATE, NEXT_TIME);
        assertEquals(NEXT_PROGRESS.getFibonacci().getCompleted(), maxDayCompleted);
        verify(progressRepository).findById(PROGRESS_ID);
        verify(progressRepository).findById(NEXT_PROGRESS_ID);
        verify(progressRepository).save(NEXT_PROGRESS);

        /*ArgumentCaptor<Progress> saveArgumentCaptor = ArgumentCaptor.forClass(Progress.class);
        verify(progressRepository, times(4)).save(saveArgumentCaptor.capture());
        System.out.println("NEXT_DATE: " + NEXT_DATE);
        System.out.println("NEXT_TIME: " + NEXT_TIME);
        System.out.println();
        System.out.println("NEXT_PROGRESS: " + NEXT_PROGRESS);
        System.out.println();
        saveArgumentCaptor.getAllValues().forEach(System.out::println);*/
    }

    @Test
    public void givenNextDateProgress_whenIncreaseProgress_thenProgressUpdated() {
        when(progressRepository.findById(any(ProgressId.class))).thenReturn(Optional.empty());
        when(progressRepository.findById(NEXT_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_PROGRESS)));

        int maxDayCompleted = incrementalProgressService.increaseProgress(USER, NEXT_DATE, LATER_TIME);
        assertEquals(LATER_PROGRESS.getIncrease().getCompleted(), maxDayCompleted);
        verify(progressRepository).findById(NEXT_PROGRESS_ID);
        verify(progressRepository).save(LATER_PROGRESS);
    }

    @Test
    public void givenNextDateLaterProgress_whenIncreaseProgress_thenProgressUpdated() {
        when(progressRepository.findById(any(ProgressId.class))).thenReturn(Optional.empty());
        when(progressRepository.findById(NEXT_PROGRESS_ID)).thenReturn(Optional.of(copyOf(LATER_PROGRESS)));

        int maxDayCompleted = incrementalProgressService.increaseProgress(USER, NEXT_DATE, EVEN_LATER_TIME);
        assertEquals(EVEN_LATER_PROGRESS.getAdd().getCompleted(), maxDayCompleted);
        verify(progressRepository).findById(NEXT_PROGRESS_ID);
        verify(progressRepository).save(EVEN_LATER_PROGRESS);
    }
}
