package org.joelson.turf.dailyinc.util;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.function.Function;

import static org.joelson.turf.dailyinc.util.ListTestUtil.createList;
import static org.joelson.turf.dailyinc.util.ListTestUtil.createListOfSize;
import static org.joelson.turf.dailyinc.util.ListTestUtil.createReversedList;
import static org.joelson.turf.dailyinc.util.ListTestUtil.verifyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListTestUtilTest {

    @Test
    public void givenStepIdIsZero_whenCreateList_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class, () -> createList(1, 1, 0, (Function<Integer, Id<Integer>>) Id::new));
        assertThrows(AssertionFailedError.class,
                () -> createReversedList(11, 1, 0, (Function<Integer, Id<Integer>>) Id::new));

        assertThrows(AssertionFailedError.class, () -> createList(1L, 1L, 0L, Id::new));
        assertThrows(AssertionFailedError.class, () -> createReversedList(1L, 1L, 0L, Id::new));
    }

    @Test
    public void givenStartIdAfterEndId_whenCreateList_thenEmptyList() {
        assertEquals(List.of(), createList(1, 0, 1, (Function<Integer, Id<Integer>>) Id::new));
        assertEquals(List.of(), createReversedList(1, 2, 1, (Function<Integer, Id<Integer>>) Id::new));

        assertEquals(List.of(), createList(1L, 0L, 1L, Id::new));
        assertEquals(List.of(), createReversedList(1L, 2L, 1L, Id::new));
    }

    @Test
    public void givenStartIdFailsPredicate_whenCreateList_thenEmptyList() {
        assertEquals(List.of(), createList('a', Id::new, c -> c < 'a', c -> (char) (c + 1)));
    }

    @Test
    public void givenSameStartIdAndEndId_whenCreateList_thenSingleElementList() {
        assertEquals(List.of(new Id<>(1)), createList(1, 1, 1, (Function<Integer, Id<Integer>>) Id::new));
        assertEquals(List.of(new Id<>(1)), createReversedList(1, 1, 1, (Function<Integer, Id<Integer>>) Id::new));

        assertEquals(List.of(new Id<>(1L)), createList(1L, 1L, 1L, Id::new));
        assertEquals(List.of(new Id<>(1L)), createReversedList(1L, 1L, 1L, Id::new));
    }

    @Test
    public void givenOnlyStartIdPassesPredicate_whenCreateList_thenSingleElementList() {
        assertEquals(List.of(new Id<>('a')), createList('a', Id::new, c -> c <= 'a', c -> (char) (c + 1)));
    }

    @Test
    public void givenDifferentStartIdAndEndId_whenCreateList_thenMultipleElementList() {
        assertEquals(List.of(new Id<>(1), new Id<>(11), new Id<>(21)),
                createList(1, 21, 10, (Function<Integer, Id<Integer>>) Id::new));
        assertEquals(List.of(new Id<>(21), new Id<>(11), new Id<>(1)),
                createReversedList(21, 1, 10, (Function<Integer, Id<Integer>>) Id::new));

        assertEquals(List.of(new Id<>(1L), new Id<>(11L), new Id<>(21L)), createList(1L, 21L, 10L, Id::new));
        assertEquals(List.of(new Id<>(21L), new Id<>(11L), new Id<>(1L)), createReversedList(21L, 1L, 10L, Id::new));
    }

    @Test
    public void givenSeveralIdPassesPredicate_whenCreateList_thenMultipleElementList() {
        assertEquals(List.of(new Id<>('a'), new Id<>('f'), new Id<>('k')),
                createList('a', Id::new, c -> c <= 'k', c -> (char) (c + 5)));
    }

    @Test
    public void givenEndIdNotModuloStepId_whenCreateList_thenLastElementIdNotEndId() {
        assertNotEquals(20, createList(1, 20, 10, (Function<Integer, Id<Integer>>) Id::new).getLast().id());
        assertNotEquals(2, createReversedList(21, 2, 10, (Function<Integer, Id<Integer>>) Id::new).getLast().id());

        assertNotEquals(20, createList(1L, 20L, 10L, Id::new).getLast().id());
        assertNotEquals(2L, createReversedList(21L, 1L, 10L, Id::new).getLast().id());
    }

    @Test
    public void givenLastIdByUpdateNotPassesPredicate_whenCreateList_thenLastElementIdIsNotLastUpdateId() {
        assertNotEquals('k', createList('a', Id::new, c -> c < 'k', c -> (char) (c + 5)).getLast().id);
    }

    @Test
    public void givenNegativeSize_whenCreateListOfSize_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class,
                () -> createListOfSize(1, 1, -1, (Function<Integer, Id<Integer>>) Id::new));
        assertThrows(AssertionFailedError.class, () -> createListOfSize(1L, 1L, -1, Id::new));
        assertThrows(AssertionFailedError.class, () -> createListOfSize('a', -1, Id::new, c -> (char) (c + 5)));
    }

    @Test
    public void givenStepIdIsZero_whenCreateListOfSize_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class,
                () -> createListOfSize(1, 0, 0, (Function<Integer, Id<Integer>>) Id::new));
        assertThrows(AssertionFailedError.class, () -> createListOfSize(1L, 0L, 0, Id::new));
    }

    @Test
    public void givenZeroSize_whenCreateListOfSize_thenEmptyList() {
        assertEquals(List.of(), createListOfSize(1, 1, 0, (Function<Integer, Id<Integer>>) Id::new));
        assertEquals(List.of(), createListOfSize(1L, 1L, 0, Id::new));
        assertEquals(List.of(), createListOfSize('a', 0, Id::new, c -> (char) (c + 5)));
    }

    @Test
    public void givenSizeAboveZero_whenCreateListOfSize_thenMultipleElementList() {
        assertEquals(List.of(new Id<>(1), new Id<>(11), new Id<>(21)),
                createListOfSize(1, 10, 3, (Function<Integer, Id<Integer>>) Id::new));
        assertEquals(List.of(new Id<>(21), new Id<>(11), new Id<>(1)),
                createListOfSize(21, -10, 3, (Function<Integer, Id<Integer>>) Id::new));

        assertEquals(List.of(new Id<>(1L), new Id<>(11L), new Id<>(21L)), createListOfSize(1L, 10L, 3, Id::new));
        assertEquals(List.of(new Id<>(21L), new Id<>(11L), new Id<>(1L)), createListOfSize(21L, -10L, 3, Id::new));

        assertEquals(List.of(new Id<>('a'), new Id<>('f'), new Id<>('k')),
                createListOfSize('a', 3, Id::new, c -> (char) (c + 5)));
    }

    @Test
    public void givenEmptyList_whenVerifyList_thenPasses() {
        verifyList(List.of(), 0, 0, 0, 0, 0, (Function<Id<Integer>, Integer>) Id::id);
        verifyList(List.of(), 0L, 0L, 0L, 0, 0, (Function<Id<Long>, Long>) Id::id);
        verifyList(List.of(), 'a', 'a', 0, 0, (Function<Id<Character>, Character>) Id::id, Character::compareTo,
                c -> (char) (c + 1));
    }

    @Test
    public void givenTooSmallList_whenVerifyList_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(), 0, 0, 0, 1, 1, (Function<Id<Integer>, Integer>) Id::id));
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(), 0L, 0L, 0L, 1, 1, (Function<Id<Long>, Long>) Id::id));
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(), 'a', 'a', 1, 1, (Function<Id<Character>, Character>) Id::id,
                        Character::compareTo, c -> (char) (c + 1)));
    }

    @Test
    public void givenTooLargeList_whenVerifyList_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(new Id<>(1)), 0, 0, 0, 0, 0, (Function<Id<Integer>, Integer>) Id::id));
        assertThrows(AssertionFailedError.class, () -> verifyList(List.of(new Id<>(1L)), 0L, 0L, 0L, 0, 0, Id::id));
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(new Id<>('a')), 'a', 'a', 0, 0, Id::id, Character::compareTo,
                        c -> (char) (c + 1)));
    }

    @Test
    public void givenTooSmallId_whenVerifyList_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(new Id<>(-1)), 0, 0, 0, 1, 1, (Function<Id<Integer>, Integer>) Id::id));
        assertThrows(AssertionFailedError.class, () -> verifyList(List.of(new Id<>(-1L)), 0L, 0L, 0L, 1, 1, Id::id));
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(new Id<>('A')), 'a', 'a', 1, 1, Id::id, Character::compareTo,
                        c -> (char) (c + 1)));
    }

    @Test
    public void givenTooLargeId_whenVerifyList_thenAssertionFailed() {
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(new Id<>(1)), 0, 0, 0, 1, 1, (Function<Id<Integer>, Integer>) Id::id));
        assertThrows(AssertionFailedError.class, () -> verifyList(List.of(new Id<>(1L)), 0L, 0L, 0L, 1, 1, Id::id));
        assertThrows(AssertionFailedError.class,
                () -> verifyList(List.of(new Id<>('b')), 'a', 'a', 1, 1, Id::id, Character::compareTo,
                        c -> (char) (c + 1)));
    }

    @Test
    public void givenOrderedList_whenVerifyList_thenPasses() {
        final int STEP = 10;

        List<Id<Integer>> orderedIntegerList = createList(1, 100, STEP, (Function<Integer, Id<Integer>>) Id::new);
        verifyList(orderedIntegerList, orderedIntegerList.getFirst().id, orderedIntegerList.getLast().id, STEP,
                orderedIntegerList.size(), orderedIntegerList.size(), (Function<Id<Integer>, Integer>) Id::id);
        List<Id<Integer>> orderedReversedIntegerList = createReversedList(100, 1, STEP,
                (Function<Integer, Id<Integer>>) Id::new);
        verifyList(orderedReversedIntegerList, orderedReversedIntegerList.getLast().id,
                orderedReversedIntegerList.getFirst().id, -STEP, orderedReversedIntegerList.size(),
                orderedReversedIntegerList.size(), (Function<Id<Integer>, Integer>) Id::id);

        List<Id<Long>> orderedLongList = createList(1L, 100L, STEP, (Function<Long, Id<Long>>) Id::new);
        verifyList(orderedLongList, orderedLongList.getFirst().id, orderedLongList.getLast().id, STEP,
                orderedLongList.size(), orderedLongList.size(), Id::id);
        List<Id<Long>> orderedReversedLongList = createReversedList(100L, 1L, STEP, (Function<Long, Id<Long>>) Id::new);
        verifyList(orderedReversedLongList, orderedReversedLongList.getLast().id, orderedReversedLongList.getFirst().id,
                -STEP, orderedReversedLongList.size(), orderedReversedLongList.size(), Id::id);

        final Function<Character, Character> STEP_FUNCTION = c -> (char) (c + 3);

        List<Id<Character>> orderedCharacterList = createList('a', (Function<Character, Id<Character>>) Id::new,
                c -> c <= 'k', STEP_FUNCTION);
        verifyList(orderedCharacterList, orderedCharacterList.getFirst().id, orderedCharacterList.getLast().id,
                orderedCharacterList.size(), orderedCharacterList.size(), Id::id, Character::compareTo, STEP_FUNCTION);
    }

    @Test
    public void givenDifferentStep_whenVerifyList_thenAssertionFailed() {
        final int STEP = 10;

        List<Id<Integer>> orderedIntegerList = createList(1, 100, STEP, (Function<Integer, Id<Integer>>) Id::new);
        assertThrows(AssertionFailedError.class,
                () -> verifyList(orderedIntegerList, orderedIntegerList.getFirst().id, orderedIntegerList.getLast().id,
                        STEP + 1, orderedIntegerList.size(), orderedIntegerList.size(),
                        (Function<Id<Integer>, Integer>) Id::id));
        List<Id<Integer>> orderedReversedIntegerList = createReversedList(100, 1, STEP,
                (Function<Integer, Id<Integer>>) Id::new);
        assertThrows(AssertionFailedError.class,
                () -> verifyList(orderedReversedIntegerList, orderedReversedIntegerList.getLast().id,
                        orderedReversedIntegerList.getFirst().id, -(STEP + 1), orderedReversedIntegerList.size(),
                        orderedReversedIntegerList.size(), (Function<Id<Integer>, Integer>) Id::id));

        List<Id<Long>> orderedLongList = createList(1L, 100L, STEP, (Function<Long, Id<Long>>) Id::new);
        assertThrows(AssertionFailedError.class,
                () -> verifyList(orderedLongList, orderedLongList.getFirst().id, orderedLongList.getLast().id, STEP + 1,
                        orderedLongList.size(), orderedLongList.size(), Id::id));
        List<Id<Long>> orderedReversedLongList = createReversedList(100L, 1L, STEP, (Function<Long, Id<Long>>) Id::new);
        assertThrows(AssertionFailedError.class,
                () -> verifyList(orderedReversedLongList, orderedReversedLongList.getLast().id,
                        orderedReversedLongList.getFirst().id, -(STEP + 1), orderedReversedLongList.size(),
                        orderedReversedLongList.size(), Id::id));

        final Function<Character, Character> STEP_FUNCTION = c -> (char) (c + 3);

        List<Id<Character>> orderedCharacterList = createList('a', (Function<Character, Id<Character>>) Id::new,
                c -> c <= 'k', STEP_FUNCTION);
        assertThrows(AssertionFailedError.class,
                () -> verifyList(orderedCharacterList, orderedCharacterList.getFirst().id,
                        orderedCharacterList.getLast().id, orderedCharacterList.size(), orderedCharacterList.size(),
                        Id::id, Character::compareTo, c -> (char) (STEP_FUNCTION.apply(c) + 1)));
    }

    private record Id<T>(T id) {
    }
}
