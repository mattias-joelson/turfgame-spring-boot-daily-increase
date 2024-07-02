package org.joelson.turf.dailyinc.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class RangeResponseUtil {

    private RangeResponseUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    static <T> ResponseEntity<List<T>> createOKResponse(String rangeUnit, List<T> list, Function<T, Integer> getter) {
        Objects.requireNonNull(getter);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit));
        if (!list.isEmpty()) {
            httpHeaders.add(HttpHeaders.CONTENT_RANGE,
                    RangeUtil.getContentRange(rangeUnit, getter.apply(list.getFirst()), getter.apply(list.getLast())));
        } else {
            httpHeaders.add(HttpHeaders.CONTENT_RANGE, RangeUtil.getUnsatisfiableContentRange(rangeUnit));
        }
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }

    static <T> ResponseEntity<List<T>> createPartialContentResponse(
            String rangeUnit, List<T> list, Function<T, Integer> getter) {
        Objects.requireNonNull(getter);
        if (list.isEmpty()) {
            return createRequestRangeNotSatisfiableResponse(rangeUnit);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit));
        httpHeaders.add(HttpHeaders.CONTENT_RANGE,
                RangeUtil.getContentRange(rangeUnit, getter.apply(list.getFirst()), getter.apply(list.getLast())));
        return new ResponseEntity<>(list, httpHeaders, HttpStatus.PARTIAL_CONTENT);
    }

    static <T> ResponseEntity<T> createRequestRangeNotSatisfiableResponse(String rangeUnit) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT_RANGES, RangeUtil.getAcceptRanges(rangeUnit));
        httpHeaders.add(HttpHeaders.CONTENT_RANGE, RangeUtil.getUnsatisfiableContentRange(rangeUnit));
        return new ResponseEntity<>(httpHeaders, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
    }
}
