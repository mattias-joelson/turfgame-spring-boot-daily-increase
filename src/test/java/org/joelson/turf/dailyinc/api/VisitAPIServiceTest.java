package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitRepository;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.dailyinc.model.Zone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitAPIServiceTest {

    @Mock
    VisitRepository visitRepository;

    @InjectMocks
    VisitAPIService visitAPIService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant TIME_LATER = TIME.plus(25, ChronoUnit.MINUTES);

    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);

    private static final User USER_ONE = new User(1001L, "UserOne", TIME);
    private static final User USER_TWO = new User(1002L, "UserTwo", TIME);
    private static final User USER_THREE = new User(1003L, "UserThree", TIME);
    private static final User USER_FOUR = new User(1004L, "UserFour", TIME);

    private static final Visit TAKE_ZONE_ONE_BY_USER_ONE = new Visit(ZONE_ONE, USER_ONE, TIME, VisitType.TAKE);
    private static final Visit ASSIST_ZONE_ONE_BY_USER_TWO = new Visit(ZONE_ONE, USER_TWO, TIME, VisitType.ASSIST);
    private static final Visit REVISIT_ZONE_TWO_BY_USER_THREE = new Visit(ZONE_TWO, USER_THREE, TIME, VisitType.REVISIT);
    private static final Visit ASSIST_ZONE_TWO_BY_USER_FOUR = new Visit(ZONE_TWO, USER_FOUR, TIME, VisitType.ASSIST);

    private static final Visit TAKE_ZONE_TWO_BY_USER_FOUR_LATER = new Visit(ZONE_TWO, USER_FOUR, TIME_LATER, VisitType.TAKE);
    private static final Visit ASSIST_ZONE_TWO_BY_USER_ONE_LATER = new Visit(ZONE_TWO, USER_ONE, TIME_LATER, VisitType.ASSIST);
    private static final Visit ASSIST_ZONE_TWO_BY_USER_TWO_LATER = new Visit(ZONE_TWO, USER_TWO, TIME_LATER, VisitType.ASSIST);
    private static final Visit TAKE_ZONE_ONE_BY_USER_THREE_LATER = new Visit(ZONE_ONE, USER_THREE, TIME_LATER, VisitType.TAKE);

    private static final List<Visit> SORTED_VISITS_LIST = List.of(TAKE_ZONE_ONE_BY_USER_ONE,
            ASSIST_ZONE_ONE_BY_USER_TWO, REVISIT_ZONE_TWO_BY_USER_THREE, ASSIST_ZONE_TWO_BY_USER_FOUR,
            TAKE_ZONE_ONE_BY_USER_THREE_LATER, TAKE_ZONE_TWO_BY_USER_FOUR_LATER, ASSIST_ZONE_TWO_BY_USER_ONE_LATER,
            ASSIST_ZONE_TWO_BY_USER_TWO_LATER);

    private static final List<Visit> USER_ONE_SORTED_VISITS = List.of(TAKE_ZONE_ONE_BY_USER_ONE, ASSIST_ZONE_TWO_BY_USER_ONE_LATER);
    private static final List<Visit> USER_TWO_SORTED_VISITS = List.of(ASSIST_ZONE_ONE_BY_USER_TWO, ASSIST_ZONE_TWO_BY_USER_TWO_LATER);
    private static final List<Visit> USER_THREE_SORTED_VISITS = List.of(REVISIT_ZONE_TWO_BY_USER_THREE, TAKE_ZONE_ONE_BY_USER_THREE_LATER);
    private static final List<Visit> USER_FOUR_SORTED_VISITS = List.of(ASSIST_ZONE_TWO_BY_USER_FOUR, TAKE_ZONE_TWO_BY_USER_FOUR_LATER);

    private static final List<Visit> ZONE_ONE_SORTED_VISITS = List.of(TAKE_ZONE_ONE_BY_USER_ONE,
            ASSIST_ZONE_ONE_BY_USER_TWO, TAKE_ZONE_ONE_BY_USER_THREE_LATER);
    private static final List<Visit> ZONE_TWO_SORTED_VISITS = List.of(REVISIT_ZONE_TWO_BY_USER_THREE,
            ASSIST_ZONE_TWO_BY_USER_FOUR, TAKE_ZONE_TWO_BY_USER_FOUR_LATER, ASSIST_ZONE_TWO_BY_USER_ONE_LATER,
            ASSIST_ZONE_TWO_BY_USER_TWO_LATER);

    @Test
    public void givenVisits_whenGetSortedVisitsBetween_thenAllReturned() {
        when(visitRepository.findSortedBetween(anyInt(), anyInt(), any())).thenReturn(List.of());
        when(visitRepository.findSortedBetween(0, SORTED_VISITS_LIST.size(), Visit.class)).thenReturn(SORTED_VISITS_LIST);

        assertEquals(SORTED_VISITS_LIST,
                visitAPIService.getSortedVisitsBetween(0, SORTED_VISITS_LIST.size() - 1, Visit.class));
        verify(visitRepository).findSortedBetween(0, SORTED_VISITS_LIST.size(), Visit.class);
    }

    @Test
    public void givenVisits_whenGetLastSortedVisits_thenAllReturned() {
        when(visitRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(visitRepository.findLastSortedReversed(SORTED_VISITS_LIST.size(), Visit.class)).thenReturn(SORTED_VISITS_LIST.reversed());

        assertEquals(SORTED_VISITS_LIST, visitAPIService.getLastSortedVisits(SORTED_VISITS_LIST.size(), Visit.class));
        verify(visitRepository).findLastSortedReversed(SORTED_VISITS_LIST.size(), Visit.class);
    }

    @Test
    public void givenVisits_whenGetSortedVisitsByUser_thenAllReturned() {
        when(visitRepository.findSortedBetweenByUser(anyLong(), anyInt(), anyInt(), any())).thenReturn(List.of());
        when(visitRepository.findSortedBetweenByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_ONE_SORTED_VISITS);
        when(visitRepository.findSortedBetweenByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_TWO_SORTED_VISITS);
        when(visitRepository.findSortedBetweenByUser(USER_THREE.getId(), 0, USER_THREE_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_THREE_SORTED_VISITS);
        when(visitRepository.findSortedBetweenByUser(USER_FOUR.getId(), 0, USER_FOUR_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_FOUR_SORTED_VISITS);

        assertEquals(USER_ONE_SORTED_VISITS,
                visitAPIService.getSortedVisitsByUser(USER_ONE.getId(), 0, USER_ONE_SORTED_VISITS.size() - 1, Visit.class));
        assertEquals(USER_TWO_SORTED_VISITS,
                visitAPIService.getSortedVisitsByUser(USER_TWO.getId(), 0, USER_TWO_SORTED_VISITS.size() - 1, Visit.class));
        assertEquals(USER_THREE_SORTED_VISITS,
                visitAPIService.getSortedVisitsByUser(USER_THREE.getId(), 0, USER_THREE_SORTED_VISITS.size() - 1, Visit.class));
        assertEquals(USER_FOUR_SORTED_VISITS,
                visitAPIService.getSortedVisitsByUser(USER_FOUR.getId(), 0, USER_FOUR_SORTED_VISITS.size() - 1, Visit.class));
        assertEquals(List.of(), visitAPIService.getSortedVisitsByUser(1005L, 0, 100, Visit.class));
        verify(visitRepository, times(5)).findSortedBetweenByUser(anyLong(), anyInt(), anyInt(), eq(Visit.class));
    }

    @Test
    public void givenVisits_whenGetLastSortedVisitsByUser_thenAllReturned() {
        when(visitRepository.findLastSortedReversedByUser(anyLong(), anyInt(), any())).thenReturn(List.of());
        when(visitRepository.findLastSortedReversedByUser(USER_ONE.getId(), USER_ONE_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_ONE_SORTED_VISITS.reversed());
        when(visitRepository.findLastSortedReversedByUser(USER_TWO.getId(), USER_TWO_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_TWO_SORTED_VISITS.reversed());
        when(visitRepository.findLastSortedReversedByUser(USER_THREE.getId(), USER_THREE_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_THREE_SORTED_VISITS.reversed());
        when(visitRepository.findLastSortedReversedByUser(USER_FOUR.getId(), USER_FOUR_SORTED_VISITS.size(), Visit.class))
                .thenReturn(USER_FOUR_SORTED_VISITS.reversed());

        assertEquals(USER_ONE_SORTED_VISITS,
                visitAPIService.getLastSortedVisitsByUser(USER_ONE.getId(), USER_ONE_SORTED_VISITS.size(), Visit.class));
        assertEquals(USER_TWO_SORTED_VISITS,
                visitAPIService.getLastSortedVisitsByUser(USER_TWO.getId(), USER_TWO_SORTED_VISITS.size(), Visit.class));
        assertEquals(USER_THREE_SORTED_VISITS,
                visitAPIService.getLastSortedVisitsByUser(USER_THREE.getId(), USER_THREE_SORTED_VISITS.size(), Visit.class));
        assertEquals(USER_FOUR_SORTED_VISITS,
                visitAPIService.getLastSortedVisitsByUser(USER_FOUR.getId(), USER_FOUR_SORTED_VISITS.size(), Visit.class));
        assertEquals(List.of(), visitAPIService.getLastSortedVisitsByUser(1005L, 100, Visit.class));
        verify(visitRepository, times(5)).findLastSortedReversedByUser(anyLong(), anyInt(), eq(Visit.class));
    }

    @Test
    public void givenVisits_whenGetSortedVisitsByZone_thenAllReturned() {
        when(visitRepository.findSortedBetweenByZone(anyLong(), anyInt(), anyInt(), any())).thenReturn(List.of());
        when(visitRepository.findSortedBetweenByZone(ZONE_ONE.getId(), 0, ZONE_ONE_SORTED_VISITS.size(), Visit.class))
                .thenReturn(ZONE_ONE_SORTED_VISITS);
        when(visitRepository.findSortedBetweenByZone(ZONE_TWO.getId(), 0, ZONE_TWO_SORTED_VISITS.size(), Visit.class))
                .thenReturn(ZONE_TWO_SORTED_VISITS);

        assertEquals(ZONE_ONE_SORTED_VISITS,
                visitAPIService.getSortedVisitsByZone(ZONE_ONE.getId(), 0, ZONE_ONE_SORTED_VISITS.size() - 1, Visit.class));
        assertEquals(ZONE_TWO_SORTED_VISITS,
                visitAPIService.getSortedVisitsByZone(ZONE_TWO.getId(), 0, ZONE_TWO_SORTED_VISITS.size() - 1, Visit.class));
        assertEquals(List.of(), visitAPIService.getSortedVisitsByZone(3L, 0, 100, Visit.class));
        verify(visitRepository, times(3)).findSortedBetweenByZone(anyLong(), anyInt(), anyInt(), eq(Visit.class));
    }

    @Test
    public void givenVisits_whenGetLastSortedVisitsByZone_thenAllReturned() {
        when(visitRepository.findLastSortedReversedByZone(anyLong(), anyInt(), any())).thenReturn(List.of());
        when(visitRepository.findLastSortedReversedByZone(ZONE_ONE.getId(), ZONE_ONE_SORTED_VISITS.size(), Visit.class))
                .thenReturn(ZONE_ONE_SORTED_VISITS.reversed());
        when(visitRepository.findLastSortedReversedByZone(ZONE_TWO.getId(), ZONE_TWO_SORTED_VISITS.size(), Visit.class))
                .thenReturn(ZONE_TWO_SORTED_VISITS.reversed());

        assertEquals(ZONE_ONE_SORTED_VISITS,
                visitAPIService.getLastSortedVisitsByZone(ZONE_ONE.getId(), ZONE_ONE_SORTED_VISITS.size(), Visit.class));
        assertEquals(ZONE_TWO_SORTED_VISITS,
                visitAPIService.getLastSortedVisitsByZone(ZONE_TWO.getId(), ZONE_TWO_SORTED_VISITS.size(), Visit.class));
        assertEquals(List.of(), visitAPIService.getLastSortedVisitsByZone(3L, 100, Visit.class));
        verify(visitRepository, times(3)).findLastSortedReversedByZone(anyLong(), anyInt(), eq(Visit.class));
    }
}
