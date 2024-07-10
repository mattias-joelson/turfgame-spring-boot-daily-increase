package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZoneAPIServiceTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);
    private static final Zone ZONE_THREE = new Zone(3L, "ZoneThree", TIME);
    @Mock
    ZoneRepository zoneRepository;
    @InjectMocks
    ZoneAPIService zoneAPIService;

    private static Zone createZone(Long id) {
        return new Zone(id, "Zone" + id, TIME);
    }

    private static List<Zone> createList(long minId, long maxId, long stepId, int limit, Predicate<Integer> sizeBeing) {
        List<Zone> zones = ListTestUtil.createList(minId, maxId, stepId, ZoneAPIServiceTest::createZone);
        return limitList(limit, sizeBeing, zones);
    }

    private static List<Zone> createReversedList(
            long maxId, long minId, long stepId, int limit, Predicate<Integer> sizeBeing) {
        List<Zone> zones = ListTestUtil.createReversedList(maxId, minId, stepId, ZoneAPIServiceTest::createZone);
        return limitList(limit, sizeBeing, zones);
    }

    private static List<Zone> limitList(int limit, Predicate<Integer> sizeBeing, List<Zone> zones) {
        if (zones.size() > limit) {
            zones = zones.subList(0, limit);
        }
        if (sizeBeing != null) {
            assertSize(zones, sizeBeing);
        }
        return Collections.unmodifiableList(zones);
    }

    private static void assertSize(List<Zone> zones, Predicate<Integer> sizeBeing) {
        assertTrue(sizeBeing.test(zones.size()), () -> String.format("zones.size()=%d", zones.size()));
    }

    private static void verifyZoneList(
            List<Zone> zones, long minId, long maxId, long stepId, int minSize, int maxSize) {
        ListTestUtil.verifyList(zones, minId, maxId, stepId, minSize, maxSize, Zone::getId);
    }

    @Test
    public void givenFewZonesInRange_whenFindAllSortedBetween_thenAllReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        long stepId = 100L;
        int limit = 100;
        when(zoneRepository.findSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findSortedBetween(minId, maxId, limit, Zone.class)).thenReturn(
                createList(minId, maxId, stepId, limit, size -> size < limit));

        List<Zone> zones = zoneAPIService.getSortedZonesBetween(minId, maxId, Zone.class);
        verifyZoneList(zones, minId, maxId, stepId, zones.size(), zones.size());
        verify(zoneRepository).findSortedBetween(minId, maxId, limit, Zone.class);
    }

    @Test
    public void givenMoreZonesInRangeThanLimit_whenFindAllSortedBetween_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 3001L;
        long stepId = 10L;
        int limit = 100;
        when(zoneRepository.findSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findSortedBetween(minId, maxId, limit, Zone.class)).thenReturn(
                createList(minId, maxId, stepId, limit, size -> size == limit));

        List<Zone> zones = zoneAPIService.getSortedZonesBetween(minId, maxId, Zone.class);
        verifyZoneList(zones, minId, maxId, stepId, limit, limit);
        verify(zoneRepository).findSortedBetween(minId, maxId, limit, Zone.class);
    }

    @Test
    public void givenZonesOutsideOfRange_whenFindAllSortedBetween_thenNoneReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        when(zoneRepository.findSortedBetween(anyLong(), anyLong(), anyInt(), any())).thenReturn(List.of());

        List<Zone> zones = zoneAPIService.getSortedZonesBetween(minId, maxId, Zone.class);
        assertTrue(zones.isEmpty());
        verify(zoneRepository).findSortedBetween(minId, maxId, 100, Zone.class);
    }

    @Test
    public void givenFewZones_whenFindLastSortedReversed_thenAllReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 100L;
        int count = 20;
        int limit = 100;
        when(zoneRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findLastSortedReversed(count, Zone.class)).thenReturn(
                createReversedList(maxId, minId, stepId, limit, size -> size < limit));

        List<Zone> zones = zoneAPIService.getLastSortedZones(count, Zone.class);
        verifyZoneList(zones, minId, maxId, stepId, zones.size(), count);
        verify(zoneRepository).findLastSortedReversed(count, Zone.class);
    }

    @Test
    public void givenManyZones_whenFindLastSortedReversed_thenLimitReturned() {
        long maxId = 2001L;
        long stepId = 10L;
        int limit = 100;
        int count = 120;
        when(zoneRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());
        when(zoneRepository.findLastSortedReversed(limit, Zone.class)).thenReturn(
                createReversedList(maxId, 1001L, stepId, limit, size -> size == limit));

        List<Zone> zones = zoneAPIService.getLastSortedZones(count, Zone.class);
        verifyZoneList(zones, zones.getFirst().getId(), maxId, stepId, limit, limit);
        verify(zoneRepository).findLastSortedReversed(limit, Zone.class);
    }

    @Test
    public void givenNoZones_whenFindLastSortedReversed_thenNoneReturned() {
        when(zoneRepository.findLastSortedReversed(anyInt(), any())).thenReturn(List.of());

        List<Zone> zones = zoneAPIService.getLastSortedZones(300, Zone.class);
        assertTrue(zones.isEmpty());
        verify(zoneRepository).findLastSortedReversed(100, Zone.class);
    }

    @Test
    public void givenZones_whenGetZoneById_thenExistingReturned() {
        when(zoneRepository.findById(anyLong(), eq(Zone.class))).thenReturn(Optional.empty());
        when(zoneRepository.findById(ZONE_ONE.getId(), Zone.class)).thenReturn(Optional.of(ZONE_ONE));
        when(zoneRepository.findById(ZONE_TWO.getId(), Zone.class)).thenReturn(Optional.of(ZONE_TWO));
        when(zoneRepository.findById(ZONE_THREE.getId(), Zone.class)).thenReturn(Optional.of(ZONE_THREE));

        assertNull(zoneAPIService.getZoneById(0L, Zone.class));
        assertEquals(ZONE_ONE, zoneAPIService.getZoneById(ZONE_ONE.getId(), Zone.class));
        assertEquals(ZONE_TWO, zoneAPIService.getZoneById(ZONE_TWO.getId(), Zone.class));
        assertEquals(ZONE_THREE, zoneAPIService.getZoneById(ZONE_THREE.getId(), Zone.class));
        assertNull(zoneAPIService.getZoneById(4L, Zone.class));
        assertNull(zoneAPIService.getZoneById(5L, Zone.class));
        verify(zoneRepository, times(6)).findById(anyLong(), eq(Zone.class));
    }

    @Test
    public void givenZones_whenGetZoneByName_thenExistingReturned() {
        when(zoneRepository.findByName(anyString(), eq(Zone.class))).thenReturn(Optional.empty());
        when(zoneRepository.findByName(ZONE_ONE.getName(), Zone.class)).thenReturn(Optional.of(ZONE_ONE));
        when(zoneRepository.findByName(ZONE_TWO.getName(), Zone.class)).thenReturn(Optional.of(ZONE_TWO));
        when(zoneRepository.findByName(ZONE_THREE.getName(), Zone.class)).thenReturn(Optional.of(ZONE_THREE));

        assertNull(zoneAPIService.getZoneByName("", Zone.class));
        assertEquals(ZONE_ONE, zoneAPIService.getZoneByName(ZONE_ONE.getName(), Zone.class));
        assertEquals(ZONE_TWO, zoneAPIService.getZoneByName(ZONE_TWO.getName(), Zone.class));
        assertEquals(ZONE_THREE, zoneAPIService.getZoneByName(ZONE_THREE.getName(), Zone.class));
        assertNull(zoneAPIService.getZoneByName(null, Zone.class));
        assertNull(zoneAPIService.getZoneByName("hej", Zone.class));
        verify(zoneRepository).findByName(null, Zone.class);
        verify(zoneRepository, times(5)).findByName(anyString(), eq(Zone.class));
    }
}
