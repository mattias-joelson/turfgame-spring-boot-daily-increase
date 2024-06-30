package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZoneAPIServiceTest {

    @Mock
    ZoneRepository zoneRepository;

    @InjectMocks
    ZoneAPIService zoneAPIService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);
    private static final Zone ZONE_THREE = new Zone(3L, "ZoneThree", TIME);

    private static final List<Zone> SORTED_ZONES_LIST = List.of(ZONE_ONE, ZONE_TWO, ZONE_THREE);

    @Test
    public void testGetSortedZones() {
        when(zoneRepository.findAllSorted(Zone.class)).thenReturn(SORTED_ZONES_LIST);

        List<Zone> sortedZones = zoneAPIService.getSortedZones(Zone.class);
        assertEquals(SORTED_ZONES_LIST, sortedZones);
        verify(zoneRepository).findAllSorted(Zone.class);
    }

    @Test
    public void testGetZoneById() {
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
    public void testGetZoneByName() {
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

    private static final Long ID = 1L;
    private static final String NAME = "Zone";
    private static final String NAME_OTHER = "ZoneOther";
    private static final Instant TIME_LATER = TIME.plusSeconds(60);

    private static final Zone ZONE = new Zone(ID, NAME, TIME);
    private static final Zone ZONE_UPDATED_TIME = new Zone(ID, NAME, TIME_LATER);
    private static final Zone ZONE_UPDATED_NAME_AND_TIME = new Zone(ID, NAME_OTHER, TIME_LATER);

    private static Zone copyOf(Zone zone) {
        return new Zone(zone.getId(), zone.getName(), zone.getTime());
    }

    @Test
    public void givenEmptyRepository_whenGetUpdateOrCreate_thenZoneCreated() {
        when(zoneRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(zoneRepository.save(any(Zone.class))).then(returnsFirstArg());

        Zone zone = zoneAPIService.getUpdateOrCreate(ID, NAME, TIME);
        assertEquals(ZONE, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository).save(ZONE);
    }

    @Test
    public void givenEqualZone_whenGetUpdateOrCreate_thenZoneNotUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE)));

        Zone zone = zoneAPIService.getUpdateOrCreate(ID, NAME, TIME);
        assertEquals(ZONE, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    public void givenOlderZone_whenGetUpdateOrCreate_thenZoneNotUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE_UPDATED_TIME)));

        Zone zone = zoneAPIService.getUpdateOrCreate(ID, NAME_OTHER, TIME);
        assertEquals(ZONE_UPDATED_TIME, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    public void givenUpdatedZone_whenGetUpdateOrCreate_thenZoneUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE)));
        when(zoneRepository.save(any(Zone.class))).then(returnsFirstArg());

        Zone zone = zoneAPIService.getUpdateOrCreate(ID, NAME_OTHER, TIME_LATER);
        assertEquals(ZONE_UPDATED_NAME_AND_TIME, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository).save(ZONE_UPDATED_NAME_AND_TIME);
    }

    @Test
    public void givenLaterZone_whenGetUpdateOrCreate_thenZoneUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE)));
        when(zoneRepository.save(any(Zone.class))).then(returnsFirstArg());

        Zone zone = zoneAPIService.getUpdateOrCreate(ID, NAME, TIME_LATER);
        assertEquals(ZONE_UPDATED_TIME, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository).save(ZONE_UPDATED_TIME);
    }
}
