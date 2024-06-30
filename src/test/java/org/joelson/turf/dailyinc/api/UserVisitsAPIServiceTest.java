package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.UserVisits;
import org.joelson.turf.dailyinc.model.UserVisitsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserVisitsAPIServiceTest {

    @Mock
    UserVisitsRepository userVisitsRepository;

    @InjectMocks
    UserVisitsAPIService userVisitsAPIService;

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

        List<UserVisits> userVisits = userVisitsAPIService.getSortedUserVisits(UserVisits.class);
        assertEquals(SORTED_USER_VISITS, userVisits);
        verify(userVisitsRepository).findAllSorted(UserVisits.class);
    }

    @Test
    public void getSortedUserVisitsByUserTest() {
        when(userVisitsRepository.findAllSortedByUser(anyLong(), eq(UserVisits.class))).thenReturn(List.of());
        when(userVisitsRepository.findAllSortedByUser(USER_ONE.getId(), UserVisits.class)).thenReturn(USER_ONE_SORTED_USER_VISITS);
        when(userVisitsRepository.findAllSortedByUser(USER_TWO.getId(), UserVisits.class)).thenReturn(USER_TWO_SORTED_USER_VISITS);

        assertEquals(USER_ONE_SORTED_USER_VISITS, userVisitsAPIService.getSortedUserVisitsByUser(USER_ONE.getId(), UserVisits.class));
        assertEquals(USER_TWO_SORTED_USER_VISITS, userVisitsAPIService.getSortedUserVisitsByUser(USER_TWO.getId(), UserVisits.class));
        assertEquals(List.of(), userVisitsAPIService.getSortedUserVisitsByUser(3L, UserVisits.class));
        verify(userVisitsRepository, times(3)).findAllSortedByUser(anyLong(), eq(UserVisits.class));
    }
}
