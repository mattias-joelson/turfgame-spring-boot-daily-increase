package org.joelson.turf.dailyinc.api;

import org.joelson.turf.dailyinc.util.ListTestUtil;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.joelson.turf.dailyinc.api.RangeResponseUtilTest.verifyPartialContentResponse;
import static org.joelson.turf.dailyinc.api.RangeResponseUtilTest.verifyRequestRangeNotSatisfiableResponse;
import static org.joelson.turf.dailyinc.api.RangeUtil.getAcceptRanges;
import static org.joelson.turf.dailyinc.api.RangeUtil.getContentRange;
import static org.joelson.turf.dailyinc.api.RangeUtil.getRange;
import static org.joelson.turf.dailyinc.api.RangeUtil.getRangeSuffix;
import static org.joelson.turf.dailyinc.api.RangeUtil.getUnsatisfiableContentRange;
import static org.joelson.turf.dailyinc.api.RangeUtil.handleRangeRequest;
import static org.joelson.turf.dailyinc.api.RangeUtil.parseRange;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RangeUtilTest {

    private static final String UNIT_RANGE = "unit";

    private static final String UNIT_RANGE_FROM_ZERO = "unit=0-";
    private static final String UNIT_RANGE_ZERO_TO_TWO = "unit=0-2";
    private static final String UNIT_RANGE_LAST_TWO = "unit=-2";

    private static final List<Id> UNIT_RANGE_FROM_ZERO_RESULT = List.of(new Id(0), new Id(Integer.MAX_VALUE));
    private static final List<Id> UNIT_RANGE_ZERO_TO_TWO_RESULT = List.of(new Id(0), new Id(2));
    private static final List<Id> UNIT_RANGE_LAST_TWO_RESULT = List.of(new Id(5), new Id(7));

    private static final BiFunction<Integer, Integer, List<Id>> BETWEEN_FUNCTION = (minId, maxId) -> List.of(new Id(minId), new Id(maxId));
    private static final Function<Integer, List<Id>> LAST_FUNCTION = last -> ListTestUtil.createListOfSize(5, 2, last, Id::new);
    private static final BiFunction<Integer, Integer, List<Id>> NO_BETWEEN_FUNCTION = (minId, maxId) -> List.of();
    private static final Function<Integer, List<Id>> NO_LAST_FUNCTION = last -> List.of();

    public static final String VALID_RANGE_VALUE_CHARACTERS = "!#$%&'*+-.^_`|~09AZaz";
    public static final String VALID_RANGE_VALUE_CHARACTERS_PLUS_RANGE = VALID_RANGE_VALUE_CHARACTERS + "=1-2";

    @Test
    public void givenInvalidRange_whenParseRange_thenParseException() {
        assertThrows(ParseException.class, () -> parseRange(null));
        assertThrows(ParseException.class, () -> parseRange(""));
        assertThrows(ParseException.class, () -> parseRange("=1-2"));
        assertThrows(ParseException.class, () -> parseRange("unit=="));
        assertThrows(ParseException.class, () -> parseRange("unit="));
        assertThrows(ParseException.class, () -> parseRange("unit=1-3-"));
        assertThrows(ParseException.class, () -> parseRange("unit=123a-"));
        assertThrows(ParseException.class, () -> parseRange("unit=-123a"));
        assertThrows(ParseException.class, () -> parseRange("unit=-"));
        assertThrows(ParseException.class, () -> parseRange("unit=-1,0"));
        assertThrows(ParseException.class, () -> parseRange("unit=-1,-1"));
        assertThrows(ParseException.class, () -> parseRange("unit=1,0"));
    }

    @Test
    public void givenRangeUnits_whenRequiresValidRangeUnit_handlesCorrectly() {
        assertThrows(IllegalArgumentException.class, () -> RangeUtil.requiresValidRangeUnit(null));
        assertThrows(IllegalArgumentException.class, () -> RangeUtil.requiresValidRangeUnit(VALID_RANGE_VALUE_CHARACTERS_PLUS_RANGE));
        assertThrows(IllegalArgumentException.class, () -> RangeUtil.requiresValidRangeUnit(""));
        assertThrows(IllegalArgumentException.class, () -> RangeUtil.requiresValidRangeUnit("unit="));

        assertEquals(VALID_RANGE_VALUE_CHARACTERS, RangeUtil.requiresValidRangeUnit(VALID_RANGE_VALUE_CHARACTERS));
        assertEquals("1-2", RangeUtil.requiresValidRangeUnit("1-2"));
        assertEquals(UNIT_RANGE, RangeUtil.requiresValidRangeUnit(UNIT_RANGE));
    }

    @Test
    public void givenValidRange_whenParseRange_thenRangeUnitMatches() throws ParseException {
        assertEquals(VALID_RANGE_VALUE_CHARACTERS, parseRange(VALID_RANGE_VALUE_CHARACTERS_PLUS_RANGE).getRangeUnit());
        assertEquals(UNIT_RANGE, parseRange("unit=1-2").getRangeUnit());
        assertEquals("1-2", parseRange("1-2=1-2").getRangeUnit());
    }

    @Test
    public void givenParseableRange_whenGetRange_thenReturnsList() throws ParseException {
        assertEquals(UNIT_RANGE_FROM_ZERO_RESULT,
                parseRange(UNIT_RANGE_FROM_ZERO).getRange(BETWEEN_FUNCTION, LAST_FUNCTION));
        assertEquals(UNIT_RANGE_ZERO_TO_TWO_RESULT,
                parseRange(UNIT_RANGE_ZERO_TO_TWO).getRange(BETWEEN_FUNCTION, LAST_FUNCTION));
        assertEquals(UNIT_RANGE_LAST_TWO_RESULT,
                parseRange(UNIT_RANGE_LAST_TWO).getRange(BETWEEN_FUNCTION, LAST_FUNCTION));
    }

    @Test
    public void givenInvalidRange_whenGetRange_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> getRange(null, 0));
        assertThrows(IllegalArgumentException.class, () -> getRange("", 0));
        assertThrows(IllegalArgumentException.class, () -> getRange("unit=", 0));
        assertThrows(IllegalArgumentException.class, () -> getRange("unit", -1));
        assertThrows(IllegalArgumentException.class, () -> getRange("unit", 10, 5));
    }

    @Test
    public void givenValidRange_whenGetRange_thenEquals() {
        assertEquals("unit=100-", getRange(UNIT_RANGE, 100));
        assertEquals("unit=5-10", getRange(UNIT_RANGE, 5, 10));
    }

    @Test
    public void givenInvalidRange_whenGetRangeSuffix_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> getRangeSuffix("unit", -1));
    }

    @Test
    public void givenValidRange_whenGetRangeSuffix_thenEquals() {
        assertEquals("unit=-100", getRangeSuffix(UNIT_RANGE, 100));
    }
    @Test
    public void givenInvalidRangeType_whenGetAcceptRanges_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> getAcceptRanges("unit="));
    }

    @Test
    public void givenValidAcceptRanges_whenGetAcceptRanges_thenEquals() {
        assertEquals(UNIT_RANGE, getAcceptRanges(UNIT_RANGE));
    }

    @Test
    public void givenInvalidContentRange_whenGetContentRange_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> getContentRange("unit=", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> getContentRange(UNIT_RANGE, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> getContentRange(UNIT_RANGE, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> getContentRange(UNIT_RANGE, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> getUnsatisfiableContentRange("unit="));
    }

    @Test
    public void givenValidContentRange_whenGetContentRange_thenEquals() {
        assertEquals("unit 0-1/*", getContentRange(UNIT_RANGE, 0, 1));
        assertEquals("unit */*", getUnsatisfiableContentRange(UNIT_RANGE));
    }

    @Test
    public void givenInvalidRangeRequest_whenHandleRangeRequest_thenRequestRangeNotSatisfiableResponse() {
        verifyRequestRangeNotSatisfiableResponse(
                UNIT_RANGE, handleRangeRequest(UNIT_RANGE, null, BETWEEN_FUNCTION, LAST_FUNCTION, Id::id));
        verifyRequestRangeNotSatisfiableResponse(
                UNIT_RANGE, handleRangeRequest(UNIT_RANGE, "zones=0,2", BETWEEN_FUNCTION, LAST_FUNCTION, Id::id)
        );
    }

    @Test
    public void givenValidRangeRequest_whenHandleRangeRequest_thenPartialContentResponse() {
        verifyPartialContentResponse(UNIT_RANGE, UNIT_RANGE_FROM_ZERO_RESULT, Id::id,
                handleRangeRequest(UNIT_RANGE, UNIT_RANGE_FROM_ZERO, BETWEEN_FUNCTION, LAST_FUNCTION, Id::id));
        verifyPartialContentResponse(UNIT_RANGE, UNIT_RANGE_ZERO_TO_TWO_RESULT, Id::id,
                handleRangeRequest(UNIT_RANGE, UNIT_RANGE_ZERO_TO_TWO, BETWEEN_FUNCTION, LAST_FUNCTION, Id::id));
        verifyPartialContentResponse(UNIT_RANGE, UNIT_RANGE_LAST_TWO_RESULT, Id::id,
                handleRangeRequest(UNIT_RANGE, UNIT_RANGE_LAST_TWO, BETWEEN_FUNCTION, LAST_FUNCTION, Id::id));
    }

    @Test
    public void givenNoExistingUnits_whenHandleRangeRequest_thenRequestRangeNotSatisfiableResponse() {
        verifyRequestRangeNotSatisfiableResponse(UNIT_RANGE,
                handleRangeRequest(UNIT_RANGE, UNIT_RANGE_FROM_ZERO, NO_BETWEEN_FUNCTION, NO_LAST_FUNCTION, Id::id));
        verifyRequestRangeNotSatisfiableResponse(UNIT_RANGE,
                handleRangeRequest(UNIT_RANGE, UNIT_RANGE_ZERO_TO_TWO, NO_BETWEEN_FUNCTION, NO_LAST_FUNCTION, Id::id));
        verifyRequestRangeNotSatisfiableResponse(UNIT_RANGE,
                handleRangeRequest(UNIT_RANGE, UNIT_RANGE_LAST_TWO, NO_BETWEEN_FUNCTION, NO_LAST_FUNCTION, Id::id));
    }

    private record Id(int id) {
    }
}
