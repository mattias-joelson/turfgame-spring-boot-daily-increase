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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
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
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);

    private static final User USER = new User(1001L, "User", TIME);
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
