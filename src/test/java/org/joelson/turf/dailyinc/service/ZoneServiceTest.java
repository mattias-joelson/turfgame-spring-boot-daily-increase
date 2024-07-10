package org.joelson.turf.dailyinc.service;

import org.joelson.turf.dailyinc.model.Zone;
import org.joelson.turf.dailyinc.model.ZoneRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZoneServiceTest {

    @Mock
    ZoneRepository zoneRepository;

    @InjectMocks
    ZoneService zoneService;

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);

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

        Zone zone = zoneService.getUpdateOrCreate(ID, NAME, TIME);
        assertEquals(ZONE, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository).save(ZONE);
    }

    @Test
    public void givenEqualZone_whenGetUpdateOrCreate_thenZoneNotUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE)));

        Zone zone = zoneService.getUpdateOrCreate(ID, NAME, TIME);
        assertEquals(ZONE, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    public void givenOlderZone_whenGetUpdateOrCreate_thenZoneNotUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE_UPDATED_TIME)));

        Zone zone = zoneService.getUpdateOrCreate(ID, NAME_OTHER, TIME);
        assertEquals(ZONE_UPDATED_TIME, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    public void givenUpdatedZone_whenGetUpdateOrCreate_thenZoneUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE)));
        when(zoneRepository.save(any(Zone.class))).then(returnsFirstArg());

        Zone zone = zoneService.getUpdateOrCreate(ID, NAME_OTHER, TIME_LATER);
        assertEquals(ZONE_UPDATED_NAME_AND_TIME, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository).save(ZONE_UPDATED_NAME_AND_TIME);
    }

    @Test
    public void givenLaterZone_whenGetUpdateOrCreate_thenZoneUpdated() {
        when(zoneRepository.findById(ID)).thenReturn(Optional.of(copyOf(ZONE)));
        when(zoneRepository.save(any(Zone.class))).then(returnsFirstArg());

        Zone zone = zoneService.getUpdateOrCreate(ID, NAME, TIME_LATER);
        assertEquals(ZONE_UPDATED_TIME, zone);
        verify(zoneRepository).findById(ID);
        verify(zoneRepository).save(ZONE_UPDATED_TIME);
    }
}
