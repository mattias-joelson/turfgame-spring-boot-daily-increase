package org.joelson.turf.dailyinc.model;

import jakarta.persistence.EntityExistsException;
import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.joelson.turf.dailyinc.util.TestEntityManagerUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ZoneRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    TestEntityManager entityManager;

    private static void verifyZoneList(
            List<Zone> zones, long minId, long maxId, long stepId, int minSize, int maxSize) {
        ListTestUtil.verifyList(zones, minId, maxId, stepId, minSize, maxSize, Zone::getId);
    }

    private int persistZones(long minId, long maxId, long stepId) {
        return TestEntityManagerUtil.persistList(entityManager, minId, maxId, stepId,
                id -> new Zone(id, "Zone" + id, TIME)).size();
    }

    @Test
    public void givenZones_whenFindByIdRaw_thenExistingReturned() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        assertEquals(ZONE_TWO, zoneRepository.findById(ZONE_TWO.getId()).orElse(null));
        assertNull(zoneRepository.findById(4711L).orElse(null));
    }

    @Test
    public void givenNewZone_whenSave_thenSaved() {
        Zone savedZone = zoneRepository.save(ZONE_ONE);
        assertEquals(ZONE_ONE, entityManager.find(Zone.class, savedZone.getId()));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(ZONE_ONE));
    }

    @Test
    public void givenZoneCreated_whenUpdate_thenUpdated() {
        Zone newZone = new Zone(1L, "Zone", TIME);
        entityManager.persist(newZone);

        String newName = "ZoneNew";
        Instant newTime = TIME.plusSeconds(1);
        newZone.setName(newName);
        newZone.setTime(newTime);
        zoneRepository.save(newZone);
        assertEquals(newZone, entityManager.find(Zone.class, newZone.getId()));
    }

    @Test
    public void givenFewZonesInRange_whenFindAllSortedBetween_thenAllReturned() {
        long minId = 1500L;
        long maxId = 2500L;
        long stepId = 100L;
        int created = persistZones(minId, maxId, stepId);
        int limit = 100;
        assertTrue(created < limit);

        List<Zone> zones = zoneRepository.findAllSortedBetween(minId, maxId, limit, Zone.class);
        verifyZoneList(zones, minId, maxId, stepId, created, created);
    }

    @Test
    public void givenMoreZonesInRangeThanLimit_whenFindAllSortedBetween_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 3001L;
        long stepId = 10L;
        int created = persistZones(minId, maxId, stepId);
        int limit = 100;
        assertTrue(created > limit);

        List<Zone> zones = zoneRepository.findAllSortedBetween(minId, maxId, limit, Zone.class);
        verifyZoneList(zones, minId, maxId, stepId, limit, limit);
    }

    @Test
    public void givenZonesOutsideOfRange_whenFindAllSortedBetween_thenNoneReturned() {
        int created = persistZones(3001L, 4001L, 10);
        assertTrue(created > 0);

        List<Zone> zones = zoneRepository.findAllSortedBetween(1500L, 2500L, 100, Zone.class);
        assertTrue(zones.isEmpty());
    }

    @Test
    public void givenFewZones_whenFindLastSortedReversed_thenAllReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 100L;
        int created = persistZones(minId, maxId, stepId);
        assertTrue(created < 100);

        List<Zone> zones = zoneRepository.findLastSortedReversed(100, Zone.class);
        verifyZoneList(zones, minId, maxId, -stepId, created, created);
    }

    @Test
    public void givenManyZones_whenFindLastSortedReversed_thenLimitReturned() {
        long minId = 1001L;
        long maxId = 2001L;
        long stepId = 10L;
        int created = persistZones(minId, maxId, stepId);
        int limit = 10;
        assertTrue(created > limit);

        List<Zone> zones = zoneRepository.findLastSortedReversed(limit, Zone.class);
        verifyZoneList(zones, minId, maxId, -stepId, limit, limit);
    }

    @Test
    public void givenNoZones_whenFindLastSortedReversed_thenNoneReturned() {
        List<Zone> zones = zoneRepository.findLastSortedReversed(10, Zone.class);
        assertTrue(zones.isEmpty());
    }

    @Test
    public void givenZones_whenFindById_thenExistingReturned() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        assertEquals(ZONE_ONE, zoneRepository.findById(ZONE_ONE.getId(), Zone.class).orElse(null));
        assertEquals(ZONE_TWO, zoneRepository.findById(ZONE_TWO.getId(), Zone.class).orElse(null));
        assertNull(zoneRepository.findById(4711L, Zone.class).orElse(null));
    }

    @Test
    public void givenZones_whenFindByName_thenExistingReturned() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        assertEquals(ZONE_ONE, zoneRepository.findByName(ZONE_ONE.getName(), Zone.class).orElse(null));
        assertEquals(ZONE_TWO, zoneRepository.findByName(ZONE_TWO.getName(), Zone.class).orElse(null));
        assertNull(zoneRepository.findByName("TestZone", Zone.class).orElse(null));
    }
}
