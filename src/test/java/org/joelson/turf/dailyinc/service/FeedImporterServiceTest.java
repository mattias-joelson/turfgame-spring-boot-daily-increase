package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Visit;
import org.joelson.turf.dailyinc.model.VisitType;
import org.joelson.turf.turfgame.apiv5.Area;
import org.joelson.turf.turfgame.apiv5.FeedTakeover;
import org.joelson.turf.turfgame.apiv5.Region;
import org.joelson.turf.turfgame.apiv5.Type;
import org.joelson.turf.dailyinc.model.User;
import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedImporterServiceTest {

    @Mock
    UserService userService;

    @Mock
    UserProgressService userProgressService;

    @Mock
    UserVisitsService userVisitsService;

    @Mock
    VisitService visitService;

    @Mock
    ZoneService zoneService;

    @InjectMocks
    FeedImporterService feedImporterService;

    private static org.joelson.turf.turfgame.apiv5.User createUser(int id, String name) {
        return new org.joelson.turf.turfgame.apiv5.User(id, name, null, null, -1, -1, null, -1, -1, null, -1, -1, -1, -1);
    }

    private static final FeedTakeover FEED_TOREKYRKA_REVISIT = new FeedTakeover("takeover", "2024-06-16T03:06:36+0000",
            new org.joelson.turf.turfgame.apiv5.Zone(17385, "TöreKyrka", new Type(9, "Holy"),
                    new Region(126, "Norrbotten", "se", new Area(1802, "Kalix kommun"), null, null),
                    65.91512, 22.653973, "2013-05-01T00:00:00+0000", 185, 1, 1693,
                    createUser(381541, "TöreHiker"), createUser(381541, "TöreHiker"), "2024-06-16T03:06:36+0000"),
            65.91512, 22.653973, null, createUser(381541, "TöreHiker"),
            new org.joelson.turf.turfgame.apiv5.User[]{ createUser(235104, "HakSme"),
                    createUser(304924, "TöreBiker") });

    private static final Instant TIME = TimeUtil.turfAPITimestampToInstant(FEED_TOREKYRKA_REVISIT.getTime());
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);

    private static final Zone ZONE_TOREKYRKA = new Zone(17385L, "TöreKyrka", TIME);

    private static final User USER_HAKSME = new User(235104L, "HakSme", TIME);
    private static final User USER_TOREBIKER = new User(304924L, "TöreBiker", TIME);
    private static final User USER_TOREHIKER = new User(381541L, "TöreHiker", TIME);

    private static final Visit REVISIT_TOREKYRKA_BY_TOREHIKER = new Visit(ZONE_TOREKYRKA, USER_TOREHIKER, TIME, VisitType.REVISIT);
    private static final Visit ASSIST_TOREKYRKA_BY_HAKSME = new Visit(ZONE_TOREKYRKA, USER_HAKSME, TIME, VisitType.ASSIST);
    private static final Visit ASSIST_TOREKYRKA_BY_TOREBIKER = new Visit(ZONE_TOREKYRKA, USER_TOREBIKER, TIME, VisitType.ASSIST);

    @Test
    public void givenServicesEmpty_whenHandleTakeoverNull_thenFailure() {
        assertThrows(NullPointerException.class, () -> feedImporterService.handleTakeover(null));
    }

    @Test
    public void givenServicesEmpty_whenHandleTakeover_thenTakeoverAdded() {
        when(zoneService.getUpdateOrCreate(anyLong(), anyString(), any(Instant.class))).thenReturn(null);
        when(zoneService.getUpdateOrCreate(ZONE_TOREKYRKA.getId(), ZONE_TOREKYRKA.getName(), ZONE_TOREKYRKA.getTime())).thenReturn(ZONE_TOREKYRKA);

        when(userService.getUpdateOrCreate(anyLong(),anyString(), any(Instant.class))).thenReturn(null);
        when(userService.getUpdateOrCreate(USER_HAKSME.getId(), USER_HAKSME.getName(), USER_HAKSME.getTime())).thenReturn(USER_HAKSME);
        when(userService.getUpdateOrCreate(USER_TOREBIKER.getId(), USER_TOREBIKER.getName(), USER_TOREBIKER.getTime())).thenReturn(USER_TOREBIKER);
        when(userService.getUpdateOrCreate(USER_TOREHIKER.getId(), USER_TOREHIKER.getName(), USER_TOREHIKER.getTime())).thenReturn(USER_TOREHIKER);

        when(visitService.getVisit(any(Zone.class), any(User.class), any(Instant.class))).thenReturn(null);
        when(visitService.create(any(Zone.class), any(User.class), any(Instant.class), any(VisitType.class))).thenReturn(null);
        when(visitService.create(REVISIT_TOREKYRKA_BY_TOREHIKER.getZone(), REVISIT_TOREKYRKA_BY_TOREHIKER.getUser(), REVISIT_TOREKYRKA_BY_TOREHIKER.getTime(), REVISIT_TOREKYRKA_BY_TOREHIKER.getType())).thenReturn(REVISIT_TOREKYRKA_BY_TOREHIKER);
        when(visitService.create(ASSIST_TOREKYRKA_BY_HAKSME.getZone(), ASSIST_TOREKYRKA_BY_HAKSME.getUser(), ASSIST_TOREKYRKA_BY_HAKSME.getTime(), ASSIST_TOREKYRKA_BY_HAKSME.getType())).thenReturn(ASSIST_TOREKYRKA_BY_HAKSME);
        when(visitService.create(ASSIST_TOREKYRKA_BY_TOREBIKER.getZone(), ASSIST_TOREKYRKA_BY_TOREBIKER.getUser(), ASSIST_TOREKYRKA_BY_TOREBIKER.getTime(), ASSIST_TOREKYRKA_BY_TOREBIKER.getType())).thenReturn(ASSIST_TOREKYRKA_BY_TOREBIKER);

        when(userVisitsService.increaseUserVisits(any(User.class), any(Instant.class))).thenReturn(-1);
        when(userVisitsService.increaseUserVisits(USER_HAKSME, DATE)).thenReturn(1);
        when(userVisitsService.increaseUserVisits(USER_TOREBIKER, DATE)).thenReturn(1);
        when(userVisitsService.increaseUserVisits(USER_TOREHIKER, DATE)).thenReturn(1);

        when(userProgressService.increaseUserProgress(any(User.class), any(Instant.class), anyInt(), any(Instant.class))).thenReturn(-1);
        when(userProgressService.increaseUserProgress(USER_HAKSME, DATE, 1, TIME)).thenReturn(1);
        when(userProgressService.increaseUserProgress(USER_TOREBIKER, DATE, 1, TIME)).thenReturn(1);
        when(userProgressService.increaseUserProgress(USER_TOREHIKER, DATE, 1, TIME)).thenReturn(1);

        feedImporterService.handleTakeover(FEED_TOREKYRKA_REVISIT);

        verify(zoneService).getUpdateOrCreate(ZONE_TOREKYRKA.getId(), ZONE_TOREKYRKA.getName(), ZONE_TOREKYRKA.getTime());
        verifyNoMoreInteractions(zoneService);

        verify(userService).getUpdateOrCreate(USER_HAKSME.getId(), USER_HAKSME.getName(), TIME);
        verify(userService).getUpdateOrCreate(USER_TOREBIKER.getId(), USER_TOREBIKER.getName(), TIME);
        verify(userService, times(2)).getUpdateOrCreate(USER_TOREHIKER.getId(), USER_TOREHIKER.getName(), TIME);
        verifyNoMoreInteractions(userService);

        verify(visitService).getVisit(REVISIT_TOREKYRKA_BY_TOREHIKER.getZone(), REVISIT_TOREKYRKA_BY_TOREHIKER.getUser(), REVISIT_TOREKYRKA_BY_TOREHIKER.getTime());
        verify(visitService).create(REVISIT_TOREKYRKA_BY_TOREHIKER.getZone(), REVISIT_TOREKYRKA_BY_TOREHIKER.getUser(), REVISIT_TOREKYRKA_BY_TOREHIKER.getTime(), REVISIT_TOREKYRKA_BY_TOREHIKER.getType());
        verify(visitService).create(ASSIST_TOREKYRKA_BY_HAKSME.getZone(), ASSIST_TOREKYRKA_BY_HAKSME.getUser(), ASSIST_TOREKYRKA_BY_HAKSME.getTime(), ASSIST_TOREKYRKA_BY_HAKSME.getType());
        verify(visitService).create(ASSIST_TOREKYRKA_BY_TOREBIKER.getZone(), ASSIST_TOREKYRKA_BY_TOREBIKER.getUser(), ASSIST_TOREKYRKA_BY_TOREBIKER.getTime(), ASSIST_TOREKYRKA_BY_TOREBIKER.getType());
        verifyNoMoreInteractions(visitService);

        verify(userVisitsService).increaseUserVisits(USER_HAKSME, DATE);
        verify(userVisitsService).increaseUserVisits(USER_TOREBIKER, DATE);
        verify(userVisitsService).increaseUserVisits(USER_TOREHIKER, DATE);
        verifyNoMoreInteractions(userVisitsService);

        verify(userProgressService).increaseUserProgress(USER_HAKSME, DATE, 1, TIME);
        verify(userProgressService).increaseUserProgress(USER_TOREBIKER, DATE, 1, TIME);
        verify(userProgressService).increaseUserProgress(USER_TOREHIKER, DATE, 1, TIME);
        verifyNoMoreInteractions(userProgressService);
    }

    @Test
    public void givenExistingVisit_whenHandleTakeover_thenTakeoverNotAdded() {
        when(zoneService.getUpdateOrCreate(anyLong(), anyString(), any(Instant.class))).thenReturn(null);
        when(zoneService.getUpdateOrCreate(ZONE_TOREKYRKA.getId(), ZONE_TOREKYRKA.getName(), ZONE_TOREKYRKA.getTime())).thenReturn(ZONE_TOREKYRKA);

        when(userService.getUpdateOrCreate(anyLong(),anyString(), any(Instant.class))).thenReturn(null);
        when(userService.getUpdateOrCreate(USER_TOREHIKER.getId(), USER_TOREHIKER.getName(), USER_TOREHIKER.getTime())).thenReturn(USER_TOREHIKER);

        when(visitService.getVisit(any(Zone.class), any(User.class), any(Instant.class))).thenReturn(null);
        when(visitService.getVisit(REVISIT_TOREKYRKA_BY_TOREHIKER.getZone(), REVISIT_TOREKYRKA_BY_TOREHIKER.getUser(), REVISIT_TOREKYRKA_BY_TOREHIKER.getTime())).thenReturn(REVISIT_TOREKYRKA_BY_TOREHIKER);

        feedImporterService.handleTakeover(FEED_TOREKYRKA_REVISIT);

        verify(zoneService).getUpdateOrCreate(ZONE_TOREKYRKA.getId(), ZONE_TOREKYRKA.getName(), ZONE_TOREKYRKA.getTime());
        verifyNoMoreInteractions(zoneService);

        verify(userService, times(2)).getUpdateOrCreate(USER_TOREHIKER.getId(), USER_TOREHIKER.getName(), TIME);
        verifyNoMoreInteractions(userService);

        verify(visitService).getVisit(REVISIT_TOREKYRKA_BY_TOREHIKER.getZone(), REVISIT_TOREKYRKA_BY_TOREHIKER.getUser(), REVISIT_TOREKYRKA_BY_TOREHIKER.getTime());
        verifyNoMoreInteractions(visitService);

        verifyNoMoreInteractions(userVisitsService);
        verifyNoMoreInteractions(userProgressService);
    }
}
