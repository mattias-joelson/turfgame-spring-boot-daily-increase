package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.DailyProgress;
import org.joelson.turf.dailyinc.model.Progress;
import org.joelson.turf.dailyinc.model.ProgressRepository;
import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkProgressServiceTest {

    private static final Instant FIRST_DATE = Instant.now().truncatedTo(ChronoUnit.DAYS);
    private static final Instant SECOND_DATE = FIRST_DATE.plus(1, ChronoUnit.DAYS);
    private static final Instant THIRD_DATE = SECOND_DATE.plus(1, ChronoUnit.DAYS);

    private static final List<Instant> FIRST_DATE_VISIT_TIMES = createVisitTimeListOfSize(FIRST_DATE, 7);
    private static final List<Instant> SECOND_DATE_VISIT_TIMES = createVisitTimeListOfSize(SECOND_DATE, 6);
    private static final List<Instant> THIRD_DATE_VISIT_TIMES = createVisitTimeListOfSize(THIRD_DATE, 9);

    private  static final List<Instant> ALL_VISIT_TIMES = concatenateLists(FIRST_DATE_VISIT_TIMES,
            SECOND_DATE_VISIT_TIMES, THIRD_DATE_VISIT_TIMES);
    private static final User USER = new User(1001L, "User", ALL_VISIT_TIMES.getLast());

    private static final DailyProgress FIRST_DAILY_PROGRESS = new DailyProgress(0, 1,
            FIRST_DATE_VISIT_TIMES.getFirst());
    private static final Progress FIRST_PROGRESS = new Progress(USER, FIRST_DATE, FIRST_DATE_VISIT_TIMES.size(),
            FIRST_DAILY_PROGRESS, FIRST_DAILY_PROGRESS, FIRST_DAILY_PROGRESS, FIRST_DAILY_PROGRESS);

    private static final DailyProgress SECOND_DAILY_PROGRESS = new DailyProgress(1, 2, SECOND_DATE_VISIT_TIMES.get(1));
    private static final Progress SECOND_PROGRESS = new Progress(USER, SECOND_DATE, SECOND_DATE_VISIT_TIMES.size(),
            SECOND_DAILY_PROGRESS, new DailyProgress(1, 2, SECOND_DATE_VISIT_TIMES.get(2)), SECOND_DAILY_PROGRESS,
            SECOND_DAILY_PROGRESS);

    private static final Progress THIRD_PROGRESS = new Progress(USER, THIRD_DATE, THIRD_DATE_VISIT_TIMES.size(),
            new DailyProgress(2, 3, THIRD_DATE_VISIT_TIMES.get(2)),
            new DailyProgress(2, 3, THIRD_DATE_VISIT_TIMES.get(5)),
            new DailyProgress(2, 3, THIRD_DATE_VISIT_TIMES.get(2)),
            new DailyProgress(2, 3, THIRD_DATE_VISIT_TIMES.get(3)));

    private static List<Instant> createVisitTimeListOfSize(Instant startTime, int size) {
        return ListTestUtil.createListOfSize(startTime, size, time -> time, instant -> instant.plusSeconds(60));
    }

    private static List<Instant> concatenateLists(List<Instant>... dayVisitsTimesLists) {
        List<Instant> allVisitTimes = new ArrayList<>();
        for (List<Instant> dayVisitTimesList : dayVisitsTimesLists) {
            allVisitTimes.addAll(dayVisitTimesList);
        }
        return allVisitTimes;
    }

    @Mock
    ProgressRepository progressRepository;

    @Mock
    VisitService visitService;

    @InjectMocks
    BulkProgressService bulkProgressService;

    @Test
    public void givenPreviousProgress_when_then() {
        when(visitService.findAllSortedVisitTimesByUser(any(User.class))).thenReturn(List.of());
        when(visitService.findAllSortedVisitTimesByUser(USER)).thenReturn(ALL_VISIT_TIMES);

        when(progressRepository.save(any(Progress.class))).thenReturn(null);
        when(progressRepository.save(FIRST_PROGRESS)).thenReturn(FIRST_PROGRESS);
        when(progressRepository.save(SECOND_PROGRESS)).thenReturn(SECOND_PROGRESS);
        when(progressRepository.save(THIRD_PROGRESS)).thenReturn(THIRD_PROGRESS);

        bulkProgressService.calculateProgressForUser(USER);
        verify(visitService).findAllSortedVisitTimesByUser(USER);
        verify(progressRepository).save(FIRST_PROGRESS);
        verify(progressRepository).save(SECOND_PROGRESS);
        verify(progressRepository).save(THIRD_PROGRESS);
    }
}
