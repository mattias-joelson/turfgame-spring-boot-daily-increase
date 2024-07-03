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
    private static final Visit TAKE = new Visit(ZONE, TAKER, TIME, VisitType.TAKE);
    private static final User ASSISTER = new User(1002L, "Assister", TIME);
    private static final Visit ASSIST = new Visit(ZONE, ASSISTER, TIME, VisitType.ASSIST);

    private static final Instant NEXT_TIME = TIME.plusSeconds(60);
    private static final Zone NEXT_ZONE = new Zone(2L, "ZoneTwo", NEXT_TIME);
    private static final Visit REVISIT = new Visit(NEXT_ZONE, TAKER, NEXT_TIME, VisitType.REVISIT);

    private static final List<Visit> SORTED_VISITS = List.of(TAKE, ASSIST, REVISIT);
    private static final List<Visit> TAKER_SORTED_VISITS = List.of(TAKE, REVISIT);
    private static final List<Visit> ASSISTER_SORTED_VISITS = List.of(ASSIST);
    private static final List<Visit> ZONE_SORTED_VISITS = List.of(TAKE, ASSIST);
    private static final List<Visit> NEXT_ZONE_SORTED_VISITS = List.of(REVISIT);

    @Autowired
    VisitRepository visitRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void givenVisits_whenFindById_thenExistingReturned() {
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
    public void givenNewVisit_whenSave_thenSaved() {
        entityManager.persist(ZONE);
        entityManager.persist(TAKER);

        Visit savedVisit = visitRepository.save(TAKE);
        assertEquals(TAKE, entityManager.find(Visit.class,
                new VisitId(savedVisit.getZone().getId(), savedVisit.getUser().getId(),
                        savedVisit.getTime())));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(TAKE));
    }

//    @Test
//    public void givenVisits_whenFindAllSorted_thenAllReturned() {
//        entityManager.persist(NEXT_ZONE);
//        entityManager.persist(TAKER);
//        entityManager.persist(REVISIT);
//        entityManager.persist(ZONE);
//        entityManager.persist(ASSISTER);
//        entityManager.persist(ASSIST);
//        entityManager.persist(TAKE);
//
//        assertEquals(SORTED_VISITS, visitRepository.findAllSorted(Limit.of(3), Visit.class));
//    }

    @Test
    public void givenVisits_whenFindAllSortedByUser_thenListReturned() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        assertEquals(TAKER_SORTED_VISITS, visitRepository.findAllSortedByUser(TAKER.getId(), Visit.class));
        assertEquals(ASSISTER_SORTED_VISITS, visitRepository.findAllSortedByUser(ASSISTER.getId(), Visit.class));
        assertEquals(List.of(), visitRepository.findAllSortedByUser(1003L, Visit.class));
    }

    @Test
    public void givenVisits_whenFindAllSortedByZone_thenListReturned() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        assertEquals(ZONE_SORTED_VISITS, visitRepository.findAllSortedByZone(ZONE.getId(), Visit.class));
        assertEquals(NEXT_ZONE_SORTED_VISITS, visitRepository.findAllSortedByZone(NEXT_ZONE.getId(), Visit.class));
        assertEquals(List.of(), visitRepository.findAllSortedByUser(3L, Visit.class));
    }
}
