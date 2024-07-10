package org.joelson.turf.dailyinc.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public final class RangeResponseUtil {

    private RangeResponseUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    static <T> ResponseEntity<List<T>> createOKResponse(String rangeUnit, List<T> list, int firstRow, int lastRow) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit));
        if (!list.isEmpty()) {
            httpHeaders.add(HttpHeaders.CONTENT_RANGE, RangeUtil.getContentRange(rangeUnit, firstRow, lastRow));
        } else {
            httpHeaders.add(HttpHeaders.CONTENT_RANGE, RangeUtil.getUnsatisfiableContentRange(rangeUnit));
        }
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }

    static <T> ResponseEntity<List<T>> createPartialContentResponse(
            String rangeUnit, List<T> list, int firstRow, int lastRow) {
        if (list.isEmpty()) {
            return createRequestRangeNotSatisfiableResponse(rangeUnit);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit));
        httpHeaders.add(HttpHeaders.CONTENT_RANGE, RangeUtil.getContentRange(rangeUnit, firstRow, lastRow));
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.PARTIAL_CONTENT);
    }

    static <T> ResponseEntity<T> createRequestRangeNotSatisfiableResponse(String rangeUnit) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit));
        httpHeaders.add(HttpHeaders.CONTENT_RANGE, RangeUtil.getUnsatisfiableContentRange(rangeUnit));
        return new ResponseEntity<>(httpHeaders, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }
}
