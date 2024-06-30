package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitId;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitServiceTest {

    @Mock
    VisitRepository visitRepository;

    @InjectMocks
    VisitService visitService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant TIME_LATER = TIME.plus(25, ChronoUnit.MINUTES);
    private static final Instant TIME_LATEST = TIME_LATER.plus(25, ChronoUnit.MINUTES);

    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);
    private static final Zone ZONE_THREE = new Zone(3L, "ZoneThree", TIME);

    private static final User USER_ONE = new User(1001L, "UserOne", TIME);
    private static final User USER_TWO = new User(1002L, "UserTwo", TIME);
    private static final User USER_THREE = new User(1003L, "UserThree", TIME);
    private static final User USER_FOUR = new User(1004L, "UserFour", TIME);
    private static final User USER_FIVE = new User(1005L, "UserFive", TIME);

    private static final Visit TAKE_ZONE_ONE_BY_USER_ONE = new Visit(ZONE_ONE, USER_ONE, TIME, VisitType.TAKE);
    private static final Visit ASSIST_ZONE_ONE_BY_USER_TWO = new Visit(ZONE_ONE, USER_TWO, TIME, VisitType.ASSIST);
    private static final Visit REVISIT_ZONE_TWO_BY_USER_THREE = new Visit(ZONE_TWO, USER_THREE, TIME, VisitType.REVISIT);
    private static final Visit ASSIST_ZONE_TWO_BY_USER_FOUR = new Visit(ZONE_TWO, USER_FOUR, TIME, VisitType.ASSIST);

    private static final Visit TAKE_ZONE_TWO_BY_USER_FOUR_LATER = new Visit(ZONE_TWO, USER_FOUR, TIME_LATER, VisitType.TAKE);
    private static final Visit ASSIST_ZONE_TWO_BY_USER_ONE_LATER = new Visit(ZONE_TWO, USER_ONE, TIME_LATER, VisitType.ASSIST);
    private static final Visit ASSIST_ZONE_TWO_BY_USER_TWO_LATER = new Visit(ZONE_TWO, USER_TWO, TIME_LATER, VisitType.ASSIST);
    private static final Visit TAKE_ZONE_ONE_BY_USER_THREE_LATER = new Visit(ZONE_ONE, USER_THREE, TIME_LATER, VisitType.TAKE);

    @Test
    public void getVisitTest() {
        when(visitRepository.findById(any())).thenReturn(Optional.empty());
        when(visitRepository.findById(new VisitId(ZONE_ONE.getId(), USER_ONE.getId(), TIME))).thenReturn(Optional.of(TAKE_ZONE_ONE_BY_USER_ONE));
        when(visitRepository.findById(new VisitId(ZONE_ONE.getId(), USER_TWO.getId(), TIME))).thenReturn(Optional.of(ASSIST_ZONE_ONE_BY_USER_TWO));
        when(visitRepository.findById(new VisitId(ZONE_TWO.getId(), USER_THREE.getId(), TIME))).thenReturn(Optional.of(REVISIT_ZONE_TWO_BY_USER_THREE));
        when(visitRepository.findById(new VisitId(ZONE_TWO.getId(), USER_FOUR.getId(), TIME))).thenReturn(Optional.of(ASSIST_ZONE_TWO_BY_USER_FOUR));
        when(visitRepository.findById(new VisitId(ZONE_ONE.getId(), USER_THREE.getId(), TIME_LATER))).thenReturn(Optional.of(TAKE_ZONE_ONE_BY_USER_THREE_LATER));
        when(visitRepository.findById(new VisitId(ZONE_TWO.getId(), USER_FOUR.getId(), TIME_LATER))).thenReturn(Optional.of(TAKE_ZONE_TWO_BY_USER_FOUR_LATER));
        when(visitRepository.findById(new VisitId(ZONE_TWO.getId(), USER_ONE.getId(), TIME_LATER))).thenReturn(Optional.of(ASSIST_ZONE_TWO_BY_USER_ONE_LATER));
        when(visitRepository.findById(new VisitId(ZONE_TWO.getId(), USER_TWO.getId(), TIME_LATER))).thenReturn(Optional.of(ASSIST_ZONE_TWO_BY_USER_TWO_LATER));

        assertEquals(TAKE_ZONE_ONE_BY_USER_ONE, visitService.getVisit(ZONE_ONE, USER_ONE, TIME));
        assertEquals(ASSIST_ZONE_ONE_BY_USER_TWO, visitService.getVisit(ZONE_ONE, USER_TWO, TIME));
        assertEquals(REVISIT_ZONE_TWO_BY_USER_THREE, visitService.getVisit(ZONE_TWO, USER_THREE, TIME));
        assertEquals(ASSIST_ZONE_TWO_BY_USER_FOUR, visitService.getVisit(ZONE_TWO, USER_FOUR, TIME));
        assertEquals(TAKE_ZONE_ONE_BY_USER_THREE_LATER, visitService.getVisit(ZONE_ONE, USER_THREE, TIME_LATER));
        assertEquals(TAKE_ZONE_TWO_BY_USER_FOUR_LATER, visitService.getVisit(ZONE_TWO, USER_FOUR, TIME_LATER));
        assertEquals(ASSIST_ZONE_TWO_BY_USER_ONE_LATER, visitService.getVisit(ZONE_TWO, USER_ONE, TIME_LATER));
        assertEquals(ASSIST_ZONE_TWO_BY_USER_TWO_LATER, visitService.getVisit(ZONE_TWO, USER_TWO, TIME_LATER));
        assertNull(visitService.getVisit(ZONE_ONE, USER_FIVE, TIME));
        assertNull(visitService.getVisit(ZONE_TWO, USER_FIVE, TIME));
        assertNull(visitService.getVisit(ZONE_ONE, USER_FIVE, TIME_LATER));
        assertNull(visitService.getVisit(ZONE_THREE, USER_ONE, TIME));
        assertNull(visitService.getVisit(ZONE_THREE, USER_TWO, TIME));
        assertNull(visitService.getVisit(ZONE_THREE, USER_THREE, TIME));
        assertNull(visitService.getVisit(ZONE_ONE, USER_ONE, TIME_LATEST));
        assertNull(visitService.getVisit(ZONE_ONE, USER_TWO, TIME_LATEST));
        assertNull(visitService.getVisit(ZONE_TWO, USER_THREE, TIME_LATEST));
        verify(visitRepository, times(17)).findById(any());
    }

    @Test
    public void createVisitTest() {
        when(visitRepository.save(any(Visit.class))).then(returnsFirstArg());

        assertEquals(TAKE_ZONE_ONE_BY_USER_ONE, visitService.create(ZONE_ONE, USER_ONE, TIME, VisitType.TAKE));
        assertEquals(ASSIST_ZONE_ONE_BY_USER_TWO, visitService.create(ZONE_ONE, USER_TWO, TIME, VisitType.ASSIST));
        assertEquals(REVISIT_ZONE_TWO_BY_USER_THREE, visitService.create(ZONE_TWO, USER_THREE, TIME, VisitType.REVISIT));
        assertEquals(ASSIST_ZONE_TWO_BY_USER_FOUR, visitService.create(ZONE_TWO, USER_FOUR, TIME, VisitType.ASSIST));
        assertEquals(TAKE_ZONE_TWO_BY_USER_FOUR_LATER, visitService.create(ZONE_TWO, USER_FOUR, TIME_LATER, VisitType.TAKE));
        assertEquals(ASSIST_ZONE_TWO_BY_USER_ONE_LATER, visitService.create(ZONE_TWO, USER_ONE, TIME_LATER, VisitType.ASSIST));
        assertEquals(ASSIST_ZONE_TWO_BY_USER_TWO_LATER, visitService.create(ZONE_TWO, USER_TWO, TIME_LATER, VisitType.ASSIST));
        assertEquals(TAKE_ZONE_ONE_BY_USER_THREE_LATER, visitService.create(ZONE_ONE, USER_THREE, TIME_LATER, VisitType.TAKE));
        verify(visitRepository, times(8)).save(any(Visit.class));
    }
}
