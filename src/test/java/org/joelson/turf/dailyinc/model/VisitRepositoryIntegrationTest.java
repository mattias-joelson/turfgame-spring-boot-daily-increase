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
public class VisitRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Zone ZONE = new Zone(1L, "Zone", TIME);
    private static final User TAKER = new User(1001L, "Taker", TIME);
    public static final Visit TAKE = new Visit(ZONE, TAKER, TIME, VisitType.TAKE);
    private static final User ASSISTER = new User(1002L, "Assister", TIME);
    public static final Visit ASSIST = new Visit(ZONE, ASSISTER, TIME, VisitType.ASSIST);

    private static final Instant NEXT_TIME = TIME.plusSeconds(60);
    private static final Zone NEXT_ZONE = new Zone(2L, "ZoneTwo", NEXT_TIME);
    private static final Visit REVISIT = new Visit(NEXT_ZONE, TAKER, NEXT_TIME, VisitType.REVISIT);

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void givenVisits_whenFindById_thenSuccess() {
        entityManager.persist(ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(ASSISTER);
        entityManager.persist(TAKE);
        entityManager.persist(ASSIST);

        Visit foundTake = visitRepository.findById(new VisitId(ZONE.getId(), TAKER.getId(), TIME)).orElse(null);
        assertEquals(TAKE, foundTake);
        Visit foundAssist = visitRepository.findById(new VisitId(ZONE.getId(), ASSISTER.getId(), TIME)).orElse(null);
        assertEquals(ASSIST, foundAssist);
        assertNull(visitRepository.findById(new VisitId(ZONE.getId(), ASSISTER.getId() + 1, TIME)).orElse(null));
        assertNull(
                visitRepository.findById(new VisitId(ZONE.getId(), TAKER.getId(), TIME.plusSeconds(1))).orElse(null));
        assertNull(visitRepository.findById(new VisitId(ZONE.getId() + 1, TAKER.getId(), TIME)).orElse(null));
    }

    @Test
    public void givenNewVisit_whenSave_thenSuccess() {
        entityManager.persist(ZONE);
        entityManager.persist(TAKER);

        Visit savedVisit = visitRepository.save(TAKE);
        assertEquals(TAKE, entityManager.find(Visit.class,
                new VisitId(savedVisit.getZone().getId(), savedVisit.getUser().getId(),
                        savedVisit.getTime())));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(TAKE));
    }

    @Test
    public void givenVisits_whenFindAllSorted_thenSuccess() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        List<Visit> visits = visitRepository.findAllSorted(Visit.class);
        assertEquals(TAKE, visits.getFirst());
        assertEquals(ASSIST, visits.get(1));
        assertEquals(REVISIT, visits.getLast());
    }

    @Test
    public void givenVisits_whenFindAllSortedByUser_thenSuccess() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        List<Visit> visits = visitRepository.findAllSortedByUser(TAKER.getId(), Visit.class);
        assertEquals(TAKE, visits.getFirst());
        assertEquals(REVISIT, visits.getLast());
    }

    @Test
    public void givenVisits_whenFindAllSortedByZone_thenSuccess() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        List<Visit> visits = visitRepository.findAllSortedByZone(ZONE.getId(), Visit.class);
        assertEquals(TAKE, visits.getFirst());
        assertEquals(ASSIST, visits.getLast());
    }
}
