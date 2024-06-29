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
public class UserVisitsRepositoryIntegrationTest {

    private static final Instant TIME = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    private static final Instant NEXT_TIME = TIME.plus(1, ChronoUnit.DAYS);
    private static final Instant DATE = TIME.truncatedTo(ChronoUnit.DAYS);
    private static final Instant NEXT_DATE = NEXT_TIME.truncatedTo(ChronoUnit.DAYS);

    private static final User USER_ONE = new User(1L, "UserOne", NEXT_TIME);
    private static final User USER_TWO = new User(2L, "UserTwo", NEXT_TIME);

    private static final UserVisits USER_ONE_VISITS = new UserVisits(USER_ONE, DATE, 15);
    private static final UserVisits USER_ONE_NEXT_VISITS = new UserVisits(USER_ONE, NEXT_DATE, 10);
    private static final UserVisits USER_TWO_VISITS = new UserVisits(USER_TWO, DATE, 3);
    private static final UserVisits USER_TWO_NEXT_VISITS = new UserVisits(USER_TWO, NEXT_DATE, 23);

    @Autowired
    UserVisitsRepository userVisitsRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void withUserVisits_whenFindById_thenSuccess() {
        entityManager.persist(USER_TWO_NEXT_VISITS);
        entityManager.persist(USER_TWO_VISITS);
        entityManager.persist(USER_ONE_NEXT_VISITS);
        entityManager.persist(USER_ONE_VISITS);

        assertEquals(USER_ONE_VISITS,
                userVisitsRepository.findById(new UserVisitsId(USER_ONE.getId(), DATE)).orElse(null));
        assertEquals(USER_ONE_NEXT_VISITS,
                userVisitsRepository.findById(new UserVisitsId(USER_ONE.getId(), NEXT_DATE)).orElse(null));
        assertEquals(USER_TWO_VISITS,
                userVisitsRepository.findById(new UserVisitsId(USER_TWO.getId(), DATE)).orElse(null));
        assertEquals(USER_TWO_NEXT_VISITS,
                userVisitsRepository.findById(new UserVisitsId(USER_TWO.getId(), NEXT_DATE)).orElse(null));

        assertNull(userVisitsRepository.findById(new UserVisitsId(USER_TWO.getId() + 1, DATE)).orElse(null));
        assertNull(userVisitsRepository.findById(new UserVisitsId(USER_TWO.getId(), NEXT_DATE.plus(1, ChronoUnit.DAYS)))
                .orElse(null));
    }

    @Test
    public void withNewUserVisits_whenSave_thenSuccess() {
        entityManager.persist(USER_ONE);

        UserVisits savedUserVisits = userVisitsRepository.save(USER_ONE_VISITS);
        assertEquals(USER_ONE_VISITS, entityManager.find(UserVisits.class,
                new UserVisitsId(savedUserVisits.getUser().getId(), savedUserVisits.getDate())));

        assertThrows(EntityExistsException.class, () -> entityManager.persist(USER_ONE_VISITS));
    }

    @Test
    public void withUserVisits_whenUpdate_thenSuccess() {
        UserVisits userVisits = new UserVisits(USER_ONE, DATE, 13);
        entityManager.persist(userVisits);

        userVisits.setVisits(17);
        UserVisits savedUserVisits = userVisitsRepository.save(userVisits);
        assertEquals(userVisits, entityManager.find(UserVisits.class,
                new UserVisitsId(savedUserVisits.getUser().getId(), savedUserVisits.getDate())));
    }

    @Test
    public void withUserVisits_whenFindAllSorted_thenSuccess() {
        entityManager.persist(USER_TWO_NEXT_VISITS);
        entityManager.persist(USER_TWO_VISITS);
        entityManager.persist(USER_ONE_NEXT_VISITS);
        entityManager.persist(USER_ONE_VISITS);

        List<UserVisits> userVisits = userVisitsRepository.findAllSorted(UserVisits.class);
        assertEquals(List.of(USER_ONE_VISITS, USER_ONE_NEXT_VISITS, USER_TWO_VISITS, USER_TWO_NEXT_VISITS), userVisits);
    }

    @Test
    public void withUserVisits_whenFindAllSortedByUser_thenSuccess() {
        entityManager.persist(USER_TWO_NEXT_VISITS);
        entityManager.persist(USER_TWO_VISITS);
        entityManager.persist(USER_ONE_NEXT_VISITS);
        entityManager.persist(USER_ONE_VISITS);

        assertEquals(List.of(USER_ONE_VISITS, USER_ONE_NEXT_VISITS),
                userVisitsRepository.findAllSortedByUser(USER_ONE.getId(), UserVisits.class));
        assertEquals(List.of(USER_TWO_VISITS, USER_TWO_NEXT_VISITS),
                userVisitsRepository.findAllSortedByUser(USER_TWO.getId(), UserVisits.class));
    }
}
