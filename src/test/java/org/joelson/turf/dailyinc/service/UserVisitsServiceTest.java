package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserVisits;
import org.joelson.turf.dailyinc.model.UserVisitsId;
import org.joelson.turf.dailyinc.model.UserVisitsRepository;
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
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserVisitsServiceTest {

    @Mock
    UserVisitsRepository userVisitsRepository;

    @InjectMocks
    UserVisitsService userVisitsService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant NEXT_TIME = TIME.plus(1, ChronoUnit.DAYS);
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Instant NEXT_DATE = NEXT_TIME.truncatedTo(ChronoUnit.DAYS);

    private static final User USER_ONE = new User(1L, "UserOne", NEXT_TIME);
    private static final User USER_TWO = new User(2L, "UserTwo", NEXT_TIME);

    private static final UserVisits USER_ONE_VISITS = new UserVisits(USER_ONE, DATE, 15);
    private static final UserVisits USER_ONE_NEXT_VISITS = new UserVisits(USER_ONE, NEXT_DATE, 10);
    private static final UserVisits USER_TWO_VISITS = new UserVisits(USER_TWO, DATE, 3);
    private static final UserVisits USER_TWO_NEXT_VISITS = new UserVisits(USER_TWO, NEXT_DATE, 23);

    private static final List<UserVisits> SORTED_USER_VISITS
            = List.of(USER_ONE_VISITS, USER_ONE_NEXT_VISITS, USER_TWO_VISITS, USER_TWO_NEXT_VISITS);
    private static final List<UserVisits> USER_ONE_SORTED_USER_VISITS = List.of(USER_ONE_VISITS, USER_ONE_NEXT_VISITS);
    private static final List<UserVisits> USER_TWO_SORTED_USER_VISITS = List.of(USER_TWO_VISITS, USER_TWO_NEXT_VISITS);

    @Test
    public void getSortedUserVisitsTest() {
        when(userVisitsRepository.findAllSorted(UserVisits.class)).thenReturn(SORTED_USER_VISITS);

        List<UserVisits> userVisits = userVisitsService.getSortedUserVisits(UserVisits.class);
        assertEquals(SORTED_USER_VISITS, userVisits);
        verify(userVisitsRepository).findAllSorted(UserVisits.class);
    }

    @Test
    public void getSortedUserVisitsByUserTest() {
        when(userVisitsRepository.findAllSortedByUser(anyLong(), eq(UserVisits.class))).thenReturn(List.of());
        when(userVisitsRepository.findAllSortedByUser(USER_ONE.getId(), UserVisits.class)).thenReturn(USER_ONE_SORTED_USER_VISITS);
        when(userVisitsRepository.findAllSortedByUser(USER_TWO.getId(), UserVisits.class)).thenReturn(USER_TWO_SORTED_USER_VISITS);

        assertEquals(USER_ONE_SORTED_USER_VISITS, userVisitsService.getSortedUserVisitsByUser(USER_ONE.getId(), UserVisits.class));
        assertEquals(USER_TWO_SORTED_USER_VISITS, userVisitsService.getSortedUserVisitsByUser(USER_TWO.getId(), UserVisits.class));
        assertEquals(List.of(), userVisitsService.getSortedUserVisitsByUser(3L, UserVisits.class));
        verify(userVisitsRepository, times(3)).findAllSortedByUser(anyLong(), eq(UserVisits.class));
    }

    private static final User USER = new User(1L, "User", TIME);
    private static final UserVisitsId USER_VISITS_ID = new UserVisitsId(USER.getId(), DATE);

    private static final UserVisits USER_VISITS = new UserVisits(USER, DATE, 1);
    private static final UserVisits NEXT_USER_VISITS = new UserVisits(USER, DATE, 2);
    private static final UserVisits LATER_USER_VISITS = new UserVisits(USER, DATE, 3);

    private static UserVisits copyOf(UserVisits userVisits) {
        return new UserVisits(userVisits.getUser(), userVisits.getDate(), userVisits.getVisits());
    }

    @Test
    public void givenEmptyRepository_whenIncreaseUserVisits_thenVisitsCreated() {
        when(userVisitsRepository.findById(any(UserVisitsId.class))).thenReturn(Optional.empty());
        when(userVisitsRepository.save(any(UserVisits.class))).then(returnsFirstArg());

        int visits = userVisitsService.increaseUserVisits(USER, DATE);
        assertEquals(USER_VISITS.getVisits(), visits);
        verify(userVisitsRepository).findById(USER_VISITS_ID);
        verify(userVisitsRepository).save(USER_VISITS);
    }

    @Test
    public void givenUserVisits_whenIncreaseUserVisits_thenVisitsUpdated() {
        when(userVisitsRepository.findById(any(UserVisitsId.class))).thenReturn(Optional.empty());
        when(userVisitsRepository.findById(USER_VISITS_ID)).thenReturn(Optional.of(copyOf(USER_VISITS)))
                .thenReturn(Optional.of(copyOf(NEXT_USER_VISITS)));

        int visits = userVisitsService.increaseUserVisits(USER, DATE);
        assertEquals(NEXT_USER_VISITS.getVisits(), visits);
        verify(userVisitsRepository).save(NEXT_USER_VISITS);

        visits = userVisitsService.increaseUserVisits(USER, DATE);
        assertEquals(LATER_USER_VISITS.getVisits(), visits);
        verify(userVisitsRepository, times(2)).findById(USER_VISITS_ID);
        verify(userVisitsRepository).save(LATER_USER_VISITS);
    }
}
