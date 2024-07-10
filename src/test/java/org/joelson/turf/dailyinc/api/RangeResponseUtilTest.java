package org.joelson.turf.dailyinc.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

import static org.joelson.turf.dailyinc.api.RangeResponseUtil.createOKResponse;
import static org.joelson.turf.dailyinc.api.RangeResponseUtil.createPartialContentResponse;
import static org.joelson.turf.dailyinc.api.RangeResponseUtil.createRequestRangeNotSatisfiableResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RangeResponseUtilTest {

    private static final String UNIT_RANGE = "unit";
    private static final List<Id> UNIT_LIST = List.of(new Id(1), new Id(3));

    public static <T> void verifyOKResponse(
            String rangeUnit, List<T> list, Function<T, Integer> getter, ResponseEntity<List<T>> response) {
        assertEquals(list, response.getBody());
        assertEquals(List.of(RangeUtil.getAcceptRanges(rangeUnit)),
                response.getHeaders().get(HttpHeaders.ACCEPT_RANGES));
        if (list.isEmpty()) {
            assertEquals(List.of(RangeUtil.getUnsatisfiableContentRange(rangeUnit)),
                    response.getHeaders().get(HttpHeaders.CONTENT_RANGE));
        } else {
            assertEquals(List.of(RangeUtil.getContentRange(rangeUnit, getter.apply(list.getFirst()), getter.apply(list.getLast()))),
                    response.getHeaders().get(HttpHeaders.CONTENT_RANGE));
        }
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public static <T> void verifyPartialContentResponse(
            String rangeUnit, List<T> list, Function<T, Integer> getter, ResponseEntity<List<T>> response) {
        assertEquals(list, response.getBody());
        assertEquals(List.of(RangeUtil.getAcceptRanges(rangeUnit)),
                response.getHeaders().get(HttpHeaders.ACCEPT_RANGES));
        assertEquals(List.of(RangeUtil.getContentRange(rangeUnit, getter.apply(list.getFirst()), getter.apply(list.getLast()))),
                response.getHeaders().get(HttpHeaders.CONTENT_RANGE));
        assertEquals(HttpStatus.PARTIAL_CONTENT, response.getStatusCode());
    }


    public static void verifyRequestRangeNotSatisfiableResponse(String rangeUnit, ResponseEntity<?> response) {
        assertNull(response.getBody());
        assertEquals(List.of(RangeUtil.getAcceptRanges(rangeUnit)),
                response.getHeaders().get(HttpHeaders.ACCEPT_RANGES));
        assertEquals(List.of(RangeUtil.getUnsatisfiableContentRange(rangeUnit)),
                response.getHeaders().get(HttpHeaders.CONTENT_RANGE));
        assertEquals(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, response.getStatusCode());
    }

    @Test
    public void givenInvalidArguments_whenCreateOKResponse_thenSomeException() {
        assertThrows(IllegalArgumentException.class, () -> createOKResponse(null, List.of(), 0, 0));
        assertThrows(IllegalArgumentException.class, () -> createOKResponse("unit=", List.of(), 0, 0));
        assertThrows(NullPointerException.class, () -> createOKResponse(UNIT_RANGE, null, 0, 0));
        assertDoesNotThrow(() -> createOKResponse(UNIT_RANGE, List.of(), -1, -1));
    }

    @Test
    public void givenValidArguments_whenCreateOKResponse_thenCorrectOKResponse() {
        verifyOKResponse(UNIT_RANGE, List.of(), Id::id, createOKResponse(UNIT_RANGE, List.of(), 0, 0));
        verifyOKResponse(UNIT_RANGE, UNIT_LIST, Id::id,
                createOKResponse(UNIT_RANGE, UNIT_LIST, UNIT_LIST.getFirst().id(), UNIT_LIST.getLast().id()));
    }

    @Test
    public void givenInvalidArguments_whenCreatePartialContentResponse_thenSomeException() {
        assertThrows(IllegalArgumentException.class, () -> createPartialContentResponse(null, List.of(), 0, 0));
        assertThrows(IllegalArgumentException.class, () -> createPartialContentResponse("unit=", List.of(), 0, 0));
        assertThrows(NullPointerException.class, () -> createPartialContentResponse(UNIT_RANGE, null, 0, 0));
        assertDoesNotThrow(() -> createPartialContentResponse(UNIT_RANGE, List.of(), -1, -1));
    }

    @Test
    public void givenValidArguments_whenCreatePartialContentResponse_thenCorrectPartialContentResponse() {
        verifyPartialContentResponse(UNIT_RANGE, UNIT_LIST, Id::id,
                createPartialContentResponse(UNIT_RANGE, UNIT_LIST, UNIT_LIST.getFirst().id(),
                        UNIT_LIST.getLast().id()));
    }

    @Test
    public void givenNoContent_whenCreatePartialContentResponse_thenRequestRangeNotSatisfiableResponse() {
        verifyRequestRangeNotSatisfiableResponse(UNIT_RANGE,
                createPartialContentResponse(UNIT_RANGE, List.of(), 0, 0));
    }

    @Test
    public void givenInvalidArguments_whenCreateRequestRangeNotSatisfiableResponse_thenSomeException() {
        assertThrows(IllegalArgumentException.class, () -> createRequestRangeNotSatisfiableResponse(null));
        assertThrows(IllegalArgumentException.class, () -> createRequestRangeNotSatisfiableResponse("unit="));
    }

    @Test
    public void givenValidArguments_whenCreateRequestRangeNotSatisfiableResponse_thenRequestRangeNotSatisfiableResponse() {
        verifyRequestRangeNotSatisfiableResponse(UNIT_RANGE, createRequestRangeNotSatisfiableResponse(UNIT_RANGE));
    }

    private record Id(int id) {
    }
}
