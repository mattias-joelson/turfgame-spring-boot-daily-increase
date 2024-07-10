package org.joelson.turf.dailyinc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ListTestUtil {

    private ListTestUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static <T> List<T> createList(int minId, int maxId, int stepId, Function<Integer, T> constructor) {
        assertTrue(stepId > 0, "stepId must be above 0");
        return createList(minId, constructor, id -> id <= maxId, id -> id + stepId);
    }

    public static <T> List<T> createList(long minId, long maxId, long stepId, Function<Long, T> constructor) {
        assertTrue(stepId > 0, "stepId must be above 0");
        return createList(minId, constructor, id -> id <= maxId, id -> id + stepId);
    }

    public static <T> List<T> createReversedList(int maxId, int minId, int stepId, Function<Integer, T> constructor) {
        assertTrue(stepId > 0, "stepId must be above 0");
        return createList(maxId, constructor, id -> id >= minId, id -> id - stepId);
    }

    public static <T> List<T> createReversedList(long maxId, long minId, long stepId, Function<Long, T> constructor) {
        assertTrue(stepId > 0, "stepId must be above 0");
        return createList(maxId, constructor, id -> id >= minId, id -> id - stepId);
    }

    public static <N, T> List<T> createList(
            N startId, Function<N, T> constructor, Predicate<N> predicate, Function<N, N> update) {
        List<T> list = new ArrayList<>();
        for (N id = startId; predicate.test(id); id = update.apply(id)) {
            list.add(constructor.apply(id));
        }
        return Collections.unmodifiableList(list);
    }

    public static <T> List<T> createListOfSize(int startId, int stepId, int size, Function<Integer, T> constructor) {
        assertTrue(stepId != 0, "stepId must not be 0");
        return createListOfSize(startId, size, constructor, id -> id + stepId);
    }

    public static <T> List<T> createListOfSize(long startId, long stepId, int size, Function<Long, T> constructor) {
        assertTrue(stepId != 0, "stepId must not be 0");
        return createListOfSize(startId, size, constructor, id -> id + stepId);
    }

    public static <N, T> List<T> createListOfSize(
            N startId, int size, Function<N, T> constructor, Function<N, N> update) {
        assertTrue(size >= 0, "size can not be negative");
        List<T> list = new ArrayList<>(size);
        for (N id = startId; list.size() < size; id = update.apply(id)) {
            list.add(constructor.apply(id));
        }
        return Collections.unmodifiableList(list);
    }

    public static <T> void verifyList(
            List<T> list, int minId, int maxId, int stepId, int minSize, int maxSize, Function<T, Integer> getter) {
        verifyList(list, minId, maxId, minSize, maxSize, getter, Integer::compareTo, id -> id + stepId);
    }

    public static <T> void verifyList(
            List<T> list, long minId, long maxId, long stepId, int minSize, int maxSize, Function<T, Long> getter) {
        verifyList(list, minId, maxId, minSize, maxSize, getter, Long::compareTo, id -> id + stepId);
    }

    public static <N, T> void verifyList(
            List<T> list, N minId, N maxId, int minSize, int maxSize, Function<T, N> getter, Comparator<N> comparator,
            Function<N, N> nextId) {
        assertTrue(minSize <= list.size() && maxSize >= list.size(),
                () -> String.format("minSize=%d, maxSize=%d, list.size()=%d", minSize, maxSize, list.size()));
        N lastId = null;
        for (T t : list) {
            N id = getter.apply(t);
            assertTrue(comparator.compare(minId, id) <= 0 && comparator.compare(id, maxId) <= 0,
                    () -> String.format("minId=%s, maxId=%s, id=%s", minId, maxId, id));
            N prevId = lastId;
            assertTrue(lastId == null || comparator.compare(id, nextId.apply(prevId)) == 0,
                    () -> String.format("prevId=%s, id=%s, expected id=%s", prevId, id, nextId.apply(prevId)));
            lastId = id;
        }
    }
}
