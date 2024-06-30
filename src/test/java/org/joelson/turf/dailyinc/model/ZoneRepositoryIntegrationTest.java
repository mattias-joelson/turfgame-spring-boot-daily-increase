package org.joelson.turf.dailyinc.model;

import jakarta.persistence.EntityExistsException;
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

@DataJpaTest
public class ZoneRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Zone ZONE_ONE = new Zone(1L, "ZoneOne", TIME);
    private static final Zone ZONE_TWO = new Zone(2L, "ZoneTwo", TIME);
    private static final List<Zone> SORTED_ZONES = List.of(ZONE_ONE, ZONE_TWO);

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    TestEntityManager entityManager;

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
    public void givenZones_whenFindAllSorted_thenAllReturned() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        assertEquals(SORTED_ZONES, zoneRepository.findAllSorted(Zone.class));
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
