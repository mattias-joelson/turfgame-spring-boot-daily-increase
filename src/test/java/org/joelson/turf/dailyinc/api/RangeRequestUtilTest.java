package org.joelson.turf.dailyinc.api;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.joelson.turf.dailyinc.api.RangeRequestUtil.handleRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RangeRequestUtilTest {

    private static final String UNIT_RANGE = "unit";

    private static final RangeRequestUtil.GetTypedBetweenFunction<Long, String> GET_BETWEEN_TO_STRING_LIST
            = (min, max, type) -> List.of(String.format("[%d, %d] of %s", min, max, type));
    private static final RangeRequestUtil.GetTypedLastFunction<String> GET_LAST_TO_STRING_LIST
            = (last, type) -> List.of(String.format("[-%d] of %s", last, type));

    @Test
    public void givenNullFunction_whenEncapsulating_thenExceptionsWhenUsed() {
        assertThrows(NullPointerException.class,
                () -> RangeRequestUtil.getBetweenOfType(null, String.class).apply(1L, 1L));
        assertThrows(NullPointerException.class,
                () -> RangeRequestUtil.getBetweenOfType(GET_BETWEEN_TO_STRING_LIST, null).apply(1L, 1L));
        assertThrows(NullPointerException.class,
                () -> RangeRequestUtil.getBetweenUsingLong(null).apply(1, 1));
        assertThrows(NullPointerException.class,
                () -> RangeRequestUtil.getLastOfType(null, String.class).apply(1));
        assertThrows(NullPointerException.class,
                () -> RangeRequestUtil.getLastOfType(GET_LAST_TO_STRING_LIST, null).apply(1));
        assertThrows(NullPointerException.class,
                () -> RangeRequestUtil.integerGetter(null).apply(""));
    }

    @Test
    public void givenLongMAX_VALUE_whenGetterApply_thenArithmeticException() {
        assertThrows(ArithmeticException.class, () -> RangeRequestUtil.integerGetter(o -> Long.MAX_VALUE).apply(""));
    }

    @Test
    public void givenInvalidArguments_whenHandleRequest_thenException() {
        assertThrows(IllegalArgumentException.class,
                () -> handleRequest(null, (min, max) -> List.of(""), Integer::parseInt));
        assertThrows(IllegalArgumentException.class,
                () -> handleRequest("", (min, max) -> List.of(""), Integer::parseInt));
        assertThrows(NullPointerException.class,
                () -> handleRequest(RangeRequestUtilTest.UNIT_RANGE, null, o -> Integer.parseInt(o.toString())));
        assertThrows(NullPointerException.class,
                () -> handleRequest(RangeRequestUtilTest.UNIT_RANGE, (min, max) -> List.of(""), null));

        assertThrows(IllegalArgumentException.class,
                () -> handleRequest(null, "", (min, max) -> List.of(""), last -> List.of(""), Integer::parseInt));
        assertThrows(IllegalArgumentException.class,
                () -> handleRequest("", "", (min, max) -> List.of(""), last -> List.of(""), Integer::parseInt));
        assertThrows(NullPointerException.class,
                () -> handleRequest(RangeRequestUtilTest.UNIT_RANGE, null, (min, max) -> List.of(""), last -> List.of(""), Integer::parseInt));
        assertThrows(NullPointerException.class,
                () -> handleRequest(RangeRequestUtilTest.UNIT_RANGE, "", null, last -> List.of(""), Integer::parseInt));
        assertThrows(NullPointerException.class,
                () -> handleRequest(RangeRequestUtilTest.UNIT_RANGE, "", (min, max) -> List.of(""), null, Integer::parseInt));
        assertThrows(NullPointerException.class,
                () -> handleRequest(RangeRequestUtilTest.UNIT_RANGE, "", (min, max) -> List.of(""), last -> List.of(""), null));
    }

    @Test
    public void givenValidParameters_whenHandleRequest_thenNothingToTest() {
        // already tested by RangeRequestUtil and RangeRequestUtil
    }
}

