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

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void givenZones_whenFindByIdRaw_thenSuccess() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        Zone foundZone = zoneRepository.findById(ZONE_TWO.getId()).orElse(null);
        assertEquals(ZONE_TWO, foundZone);
        assertNull(zoneRepository.findById(4711L).orElse(null));
    }

    @Test
    public void givenNewZone_whenSave_thenSuccess() {
        Zone savedZone = zoneRepository.save(ZONE_ONE);
        assertEquals(ZONE_ONE, entityManager.find(Zone.class, savedZone.getId()));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(ZONE_ONE));
    }

    @Test
    public void givenZoneCreated_whenUpdate_thenSuccess() {
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
    public void givenZones_whenFindAllSorted_thenSuccess() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        List<Zone> sortedZones = zoneRepository.findAllSorted(Zone.class);
        assertEquals(ZONE_ONE, sortedZones.getFirst());
        assertEquals(ZONE_TWO, sortedZones.getLast());
    }

    @Test
    public void givenZones_whenFindById_thenSuccess() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        Zone foundZone = zoneRepository.findById(ZONE_TWO.getId(), Zone.class).orElse(null);
        assertEquals(ZONE_TWO, foundZone);
        assertNull(zoneRepository.findById(4711L, Zone.class).orElse(null));
    }

    @Test
    public void givenZones_whenFindByName_thenSuccess() {
        entityManager.persist(ZONE_TWO);
        entityManager.persist(ZONE_ONE);

        Zone foundZone = zoneRepository.findByName(ZONE_TWO.getName(), Zone.class).orElse(null);
        assertEquals(ZONE_TWO, foundZone);
        assertNull(zoneRepository.findByName("TestZone", Zone.class).orElse(null));
    }
}
