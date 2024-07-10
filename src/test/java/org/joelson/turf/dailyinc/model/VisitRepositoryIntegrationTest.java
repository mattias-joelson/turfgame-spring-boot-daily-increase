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
    private static final List<Visit> SORTED_VISITS_REVERSED = SORTED_VISITS.reversed();
    private static final List<Visit> TAKER_SORTED_VISITS = List.of(TAKE, REVISIT);
    private static final List<Visit> TAKER_SORTED_VISITS_REVERSED = TAKER_SORTED_VISITS.reversed();
    private static final List<Visit> ASSISTER_SORTED_VISITS = List.of(ASSIST);
    private static final List<Visit> ZONE_SORTED_VISITS = List.of(TAKE, ASSIST);
    private static final List<Visit> ZONE_SORTED_VISITS_REVERSED = ZONE_SORTED_VISITS.reversed();
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

    @Test
    public void givenVisits_whenFindSortedBetween_thenListReturned() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        assertEquals(SORTED_VISITS, visitRepository.findSortedBetween(0, SORTED_VISITS.size(), Visit.class));
        assertEquals(SORTED_VISITS.subList(1, 2), visitRepository.findSortedBetween(1, 1, Visit.class));
        assertEquals(List.of(), visitRepository.findSortedBetween(SORTED_VISITS.size(), 100, Visit.class));

        assertEquals(SORTED_VISITS_REVERSED, visitRepository.findLastSortedReversed(SORTED_VISITS.size(), Visit.class));
        assertEquals(SORTED_VISITS_REVERSED.subList(0, 2), visitRepository.findLastSortedReversed(2, Visit.class));
        assertEquals(List.of(), visitRepository.findLastSortedReversed(0, Visit.class));
    }

    @Test
    public void givenVisits_whenFindSortedBetweenByUser_thenListReturned() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        assertEquals(TAKER_SORTED_VISITS,
                visitRepository.findSortedBetweenByUser(TAKER.getId(), 0, TAKER_SORTED_VISITS.size(), Visit.class));
        assertEquals(TAKER_SORTED_VISITS.subList(1, TAKER_SORTED_VISITS.size()),
                visitRepository.findSortedBetweenByUser(TAKER.getId(), 1, 100, Visit.class));
        assertEquals(ASSISTER_SORTED_VISITS,
                visitRepository.findSortedBetweenByUser(ASSISTER.getId(), 0, ASSISTER_SORTED_VISITS.size(), Visit.class));
        assertEquals(List.of(), visitRepository.findSortedBetweenByUser(1003L, 0, 100, Visit.class));

        assertEquals(TAKER_SORTED_VISITS_REVERSED,
                visitRepository.findLastSortedReversedByUser(TAKER.getId(), TAKER_SORTED_VISITS_REVERSED.size(), Visit.class));
        assertEquals(List.of(),
                visitRepository.findLastSortedReversedByUser(TAKER.getId(), 0, Visit.class));
        assertEquals(ASSISTER_SORTED_VISITS,
                visitRepository.findLastSortedReversedByUser(ASSISTER.getId(), ASSISTER_SORTED_VISITS.size(), Visit.class));
        assertEquals(List.of(), visitRepository.findLastSortedReversedByUser(1003L, 100, Visit.class));
    }

    @Test
    public void givenVisits_whenFindSortedBetweenByZone_thenListReturned() {
        entityManager.persist(NEXT_ZONE);
        entityManager.persist(TAKER);
        entityManager.persist(REVISIT);
        entityManager.persist(ZONE);
        entityManager.persist(ASSISTER);
        entityManager.persist(ASSIST);
        entityManager.persist(TAKE);

        assertEquals(ZONE_SORTED_VISITS,
                visitRepository.findSortedBetweenByZone(ZONE.getId(), 0, ZONE_SORTED_VISITS.size(), Visit.class));
        assertEquals(ZONE_SORTED_VISITS.subList(1, ZONE_SORTED_VISITS.size()),
                visitRepository.findSortedBetweenByZone(ZONE.getId(), 1, 100, Visit.class));
        assertEquals(NEXT_ZONE_SORTED_VISITS,
                visitRepository.findSortedBetweenByZone(NEXT_ZONE.getId(), 0, NEXT_ZONE_SORTED_VISITS.size(), Visit.class));
        assertEquals(List.of(), visitRepository.findSortedBetweenByZone(3L, 0, 100, Visit.class));

        assertEquals(ZONE_SORTED_VISITS_REVERSED,
                visitRepository.findLastSortedReversedByZone(ZONE.getId(), ZONE_SORTED_VISITS_REVERSED.size(), Visit.class));
        assertEquals(List.of(),
                visitRepository.findLastSortedReversedByZone(ZONE.getId(), 0, Visit.class));
        assertEquals(NEXT_ZONE_SORTED_VISITS,
                visitRepository.findLastSortedReversedByZone(NEXT_ZONE.getId(), NEXT_ZONE_SORTED_VISITS.size(), Visit.class));
        assertEquals(List.of(), visitRepository.findLastSortedReversedByZone(3L, 100, Visit.class));
    }
}
