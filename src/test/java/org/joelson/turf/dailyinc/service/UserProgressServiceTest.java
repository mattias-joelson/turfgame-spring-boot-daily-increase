package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserProgress;
import org.joelson.turf.dailyinc.model.UserProgressId;
import org.joelson.turf.dailyinc.model.UserProgressRepository;
import org.joelson.turf.dailyinc.model.UserProgressType;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProgressServiceTest {

    @Mock
    UserProgressRepository userProgressRepository;

    @InjectMocks
    UserProgressService userProgressService;

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

    private static final UserProgressId USER_INC_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_INCREASE, DATE);
    private static final UserProgressId USER_ADD_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_ADD, DATE);
    private static final UserProgressId USER_FIB_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_FIBONACCI, DATE);
    private static final UserProgressId USER_POW_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_POWER_OF_TWO, DATE);

    private static final UserProgress USER_INC_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_INCREASE, DATE, 0, 1, TIME);
    private static final UserProgress USER_ADD_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_ADD, DATE, 0, 1, TIME);
    private static final UserProgress USER_FIB_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_FIBONACCI, DATE, 0, 1, TIME);
    private static final UserProgress USER_POW_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_POWER_OF_TWO, DATE, 0, 1, TIME);

    private static final UserProgressId NEXT_USER_INC_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_INCREASE, NEXT_DATE);
    private static final UserProgressId NEXT_USER_ADD_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_ADD, NEXT_DATE);
    private static final UserProgressId NEXT_USER_FIB_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_FIBONACCI, NEXT_DATE);
    private static final UserProgressId NEXT_USER_POW_PROGRESS_ID = new UserProgressId(USER.getId(), UserProgressType.DAILY_POWER_OF_TWO, NEXT_DATE);

    private static final UserProgress NEXT_USER_INC_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_INCREASE, NEXT_DATE, 1, 1, NEXT_TIME);
    private static final UserProgress NEXT_USER_ADD_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_ADD, NEXT_DATE, 1, 1, NEXT_TIME);
    private static final UserProgress NEXT_USER_FIB_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_FIBONACCI, NEXT_DATE, 1, 2, NEXT_TIME);
    private static final UserProgress NEXT_USER_POW_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_POWER_OF_TWO, NEXT_DATE, 1, 1, NEXT_TIME);

    private static final Instant LATER_TIME = NEXT_TIME.plusSeconds(93);

    private static final UserProgress LATER_USER_INC_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_INCREASE, NEXT_DATE, 1, 2, LATER_TIME);
    private static final UserProgress LATER_USER_POW_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_POWER_OF_TWO, NEXT_DATE, 1, 2, LATER_TIME);

    private static final Instant EVEN_LATER_TIME = LATER_TIME.plusSeconds(129);

    private static final UserProgress EVEN_LATER_USER_ADD_PROGRESS = new UserProgress(USER, UserProgressType.DAILY_ADD, NEXT_DATE, 1, 2, EVEN_LATER_TIME);

    private static UserProgress copyOf(UserProgress userProgress) {
        return new UserProgress(userProgress.getUser(), userProgress.getType(), userProgress.getDate(), userProgress.getPreviousDayCompleted(), userProgress.getDayCompleted(), userProgress.getTimeCompleted());
    }

    @Test
    public void givenEmptyRepository_whenIncreaseUserProgress_thenProgressCreated() {
        when(userProgressRepository.findById(any(UserProgressId.class))).thenReturn(Optional.empty());

        int maxDayCompleted = userProgressService.increaseUserProgress(USER, DATE, 1, TIME);
        assertEquals(USER_INC_PROGRESS.getDayCompleted(), maxDayCompleted);
        verify(userProgressRepository).findById(USER_INC_PROGRESS_ID);
        verify(userProgressRepository).save(USER_INC_PROGRESS);
        verify(userProgressRepository).findById(USER_ADD_PROGRESS_ID);
        verify(userProgressRepository).save(USER_ADD_PROGRESS);
        verify(userProgressRepository).findById(USER_FIB_PROGRESS_ID);
        verify(userProgressRepository).save(USER_FIB_PROGRESS);
        verify(userProgressRepository).findById(USER_POW_PROGRESS_ID);
        verify(userProgressRepository).save(USER_POW_PROGRESS);
    }

    @Test
    public void givenDateUserProgress_whenIncreaseUserProgress_thenProgressNotUpdated() {
        when(userProgressRepository.findById(any(UserProgressId.class))).thenReturn(Optional.empty());
        when(userProgressRepository.findById(USER_INC_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_INC_PROGRESS)));
        when(userProgressRepository.findById(USER_ADD_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_ADD_PROGRESS)));
        when(userProgressRepository.findById(USER_FIB_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_FIB_PROGRESS)));
        when(userProgressRepository.findById(USER_POW_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_POW_PROGRESS)));

        int maxDayCompleted = userProgressService.increaseUserProgress(USER, DATE, 2, TIME.plusSeconds(60));
        assertEquals(USER_INC_PROGRESS.getDayCompleted(), maxDayCompleted);
        verify(userProgressRepository).findById(USER_INC_PROGRESS_ID);
        verify(userProgressRepository).findById(USER_ADD_PROGRESS_ID);
        verify(userProgressRepository).findById(USER_FIB_PROGRESS_ID);
        verify(userProgressRepository).findById(USER_POW_PROGRESS_ID);
        verify(userProgressRepository, never()).save(any(UserProgress.class));
    }

    @Test
    public void givenDateUserProgressButNoNextDateUserProgress_whenIncreaseUserProgress_thenProgressCreated() {
        when(userProgressRepository.findById(any(UserProgressId.class))).thenReturn(Optional.empty());
        when(userProgressRepository.findById(USER_INC_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_INC_PROGRESS)));
        when(userProgressRepository.findById(USER_ADD_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_ADD_PROGRESS)));
        when(userProgressRepository.findById(USER_FIB_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_FIB_PROGRESS)));
        when(userProgressRepository.findById(USER_POW_PROGRESS_ID)).thenReturn(Optional.of(copyOf(USER_POW_PROGRESS)));

        int maxDayCompleted = userProgressService.increaseUserProgress(USER, NEXT_DATE, 1, NEXT_TIME);
        assertEquals(NEXT_USER_FIB_PROGRESS.getDayCompleted(), maxDayCompleted);
        verify(userProgressRepository).findById(USER_INC_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_INC_PROGRESS_ID);
        verify(userProgressRepository).save(NEXT_USER_INC_PROGRESS);
        verify(userProgressRepository).findById(USER_ADD_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_ADD_PROGRESS_ID);
        verify(userProgressRepository).save(NEXT_USER_ADD_PROGRESS);
        verify(userProgressRepository).findById(USER_FIB_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_FIB_PROGRESS_ID);
        verify(userProgressRepository).save(NEXT_USER_FIB_PROGRESS);
        verify(userProgressRepository).findById(USER_POW_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_POW_PROGRESS_ID);
        verify(userProgressRepository).save(NEXT_USER_POW_PROGRESS);

        /*ArgumentCaptor<UserProgress> saveArgumentCaptor = ArgumentCaptor.forClass(UserProgress.class);
        verify(userProgressRepository, times(4)).save(saveArgumentCaptor.capture());
        System.out.println("NEXT_DATE: " + NEXT_DATE);
        System.out.println("NEXT_TIME: " + NEXT_TIME);
        System.out.println();
        System.out.println("NEXT_USER_INC_PROGRESS: " + NEXT_USER_INC_PROGRESS);
        System.out.println("NEXT_USER_ADD_PROGRESS: " + NEXT_USER_ADD_PROGRESS);
        System.out.println("NEXT_USER_FIB_PROGRESS: " + NEXT_USER_FIB_PROGRESS);
        System.out.println("NEXT_USER_POW_PROGRESS: " + NEXT_USER_POW_PROGRESS);
        System.out.println();
        saveArgumentCaptor.getAllValues().forEach(System.out::println);*/
    }

    @Test
    public void givenNextDateUserProgress_whenIncreaseUserProgress_thenProgressUpdated() {
        when(userProgressRepository.findById(any(UserProgressId.class))).thenReturn(Optional.empty());
        when(userProgressRepository.findById(NEXT_USER_INC_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_USER_INC_PROGRESS)));
        when(userProgressRepository.findById(NEXT_USER_ADD_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_USER_ADD_PROGRESS)));
        when(userProgressRepository.findById(NEXT_USER_FIB_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_USER_FIB_PROGRESS)));
        when(userProgressRepository.findById(NEXT_USER_POW_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_USER_POW_PROGRESS)));

        int maxDayCompleted = userProgressService.increaseUserProgress(USER, NEXT_DATE, 2, LATER_TIME);
        assertEquals(NEXT_USER_FIB_PROGRESS.getDayCompleted(), maxDayCompleted);
        verify(userProgressRepository).findById(NEXT_USER_INC_PROGRESS_ID);
        verify(userProgressRepository).save(LATER_USER_INC_PROGRESS);
        verify(userProgressRepository).findById(NEXT_USER_ADD_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_FIB_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_POW_PROGRESS_ID);
        verify(userProgressRepository).save(LATER_USER_POW_PROGRESS);
    }

    @Test
    public void givenNextDateLaterUserProgress_whenIncreaseUserProgress_thenProgressUpdated() {
        when(userProgressRepository.findById(any(UserProgressId.class))).thenReturn(Optional.empty());
        when(userProgressRepository.findById(NEXT_USER_INC_PROGRESS_ID)).thenReturn(Optional.of(copyOf(LATER_USER_INC_PROGRESS)));
        when(userProgressRepository.findById(NEXT_USER_ADD_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_USER_ADD_PROGRESS)));
        when(userProgressRepository.findById(NEXT_USER_FIB_PROGRESS_ID)).thenReturn(Optional.of(copyOf(NEXT_USER_FIB_PROGRESS)));
        when(userProgressRepository.findById(NEXT_USER_POW_PROGRESS_ID)).thenReturn(Optional.of(copyOf(LATER_USER_POW_PROGRESS)));

        int maxDayCompleted = userProgressService.increaseUserProgress(USER, NEXT_DATE, 3, EVEN_LATER_TIME);
        assertEquals(NEXT_USER_FIB_PROGRESS.getDayCompleted(), maxDayCompleted);
        verify(userProgressRepository).findById(NEXT_USER_INC_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_ADD_PROGRESS_ID);
        verify(userProgressRepository).save(EVEN_LATER_USER_ADD_PROGRESS);
        verify(userProgressRepository).findById(NEXT_USER_FIB_PROGRESS_ID);
        verify(userProgressRepository).findById(NEXT_USER_POW_PROGRESS_ID);
    }
}
