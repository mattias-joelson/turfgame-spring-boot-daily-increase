package org.joelson.turf.dailyinc.util;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.joelson.turf.dailyinc.util.ListTestUtil.createList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestEntityManagerUtil {

    private TestEntityManagerUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static <T> List<T> persistList(
            TestEntityManager entityManager, int minId, int maxId, int stepId, Function<Integer, T> constructor) {
        return persistList(entityManager, createList(minId, maxId, stepId, constructor));
    }

    public static <T> List<T> persistList(
            TestEntityManager entityManager, long minId, long maxId, long stepId, Function<Long, T> constructor) {
        return persistList(entityManager, createList(minId, maxId, stepId, constructor));
    }

    public static <T> List<T> persistReversedList(
            TestEntityManager entityManager, int maxId, int minId, int stepId, Function<Integer, T> constructor) {
        return persistList(entityManager, createList(maxId, minId, stepId, constructor));
    }

    public static <T> List<T> persistReversedList(
            TestEntityManager entityManager, long maxId, long minId, long stepId, Function<Long, T> constructor) {
        return persistList(entityManager, createList(maxId, minId, stepId, constructor));
    }

    public static <N, T> List<T> persistList(
            TestEntityManager entityManager, N startId, Function<N, T> constructor, Predicate<N> predicate,
            Function<N, N> update) {
        return persistList(entityManager, createList(startId, constructor, predicate, update));
    }

    public static <T> List<T> persistList(TestEntityManager entityManager, List<T> list) {
        List<T> persistedList = new ArrayList<>(list.size());
        for (T t : list) {
            persistedList.add(entityManager.persist(t));
        }
        return Collections.unmodifiableList(persistedList);
    }

    public static <N, T> void verifyPersistedList(
            TestEntityManager entityManager, Class<T> type, List<T> list, Function<T, N> getter) {
        for (T t : list) {
            assertEquals(t, entityManager.find(type, getter.apply(t)));
        }
    }
}
