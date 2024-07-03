package org.joelson.turf.dailyinc.api;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RangeRequestUtil {

    private RangeRequestUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static <T> ResponseEntity<List<T>> handleIdRequest(
            String rangeUnit, Class<T> type, GetTypedBetweenFunction<Long, T> getBetween, Function<T, Long> getter) {
        return handleIdRequest(rangeUnit, getBetweenOfType(getBetween, type), getter);
    }

    public static <T> ResponseEntity<List<T>> handleIdRequest(
            String rangeUnit, String rangeHeader, Class<T> type, GetTypedBetweenFunction<Long, T> getBetween,
            GetTypedLastFunction<T> getLast, Function<T, Long> getter) {
        return handleIdRequest(rangeUnit, rangeHeader, getBetweenOfType(getBetween, type), getLastOfType(getLast, type), getter);
    }

    public static <T> ResponseEntity<List<T>> handleIdRequest(
            String rangeUnit, BiFunction<Long, Long, List<T>> getBetween, Function<T, Long> getter) {
        return handleRequest(rangeUnit, getBetweenUsingLong(getBetween), integerGetter(getter));
    }

    public static <T> ResponseEntity<List<T>> handleIdRequest(
            String rangeUnit, String rangeHeader, BiFunction<Long, Long, List<T>> getBetween,
            Function<Integer, List<T>> getLast, Function<T, Long> getter) {
        return handleRequest(rangeUnit, rangeHeader, getBetweenUsingLong(getBetween), getLast, integerGetter(getter));
    }

    public static <T> ResponseEntity<List<T>> handleRequest(
            String rangeUnit, BiFunction<Integer, Integer, List<T>> getBetween, Function<T, Integer> getter) {
        RangeUtil.requiresValidRangeUnit(rangeUnit);
        Objects.requireNonNull(getBetween);
        Objects.requireNonNull(getter);
        List<T> body = getBetween.apply(0, Integer.MAX_VALUE);
        return RangeResponseUtil.createOKResponse(rangeUnit, body, getter);
    }

    public static <T> ResponseEntity<List<T>> handleRequest(
            String rangeUnit, Class<T> type, GetTypedBetweenFunction<Integer, T> getBetween) {
        RangeUtil.requiresValidRangeUnit(rangeUnit);
        Objects.requireNonNull(getBetween);
        return handleRequest(rangeUnit, getBetweenOfType(getBetween, type));
    }

    public static <T> ResponseEntity<List<T>> handleRequest(
            String rangeUnit, BiFunction<Integer, Integer, List<T>> getBetween) {
        RangeUtil.requiresValidRangeUnit(rangeUnit);
        Objects.requireNonNull(getBetween);
        List<T> body = getBetween.apply(0, Integer.MAX_VALUE);
        return RangeResponseUtil.createOKResponse(rangeUnit, body, 1, body.size());
    }

    public static <T> ResponseEntity<List<T>> handleRequest(
            String rangeUnit, String rangeHeader, BiFunction<Integer, Integer, List<T>> getBetween,
            Function<Integer, List<T>> getLast, Function<T, Integer> getter) {
        RangeUtil.requiresValidRangeUnit(rangeUnit);
        Objects.requireNonNull(rangeHeader);
        Objects.requireNonNull(getBetween);
        Objects.requireNonNull(getLast);
        Objects.requireNonNull(getter);
        return RangeUtil.handleRangeRequest(rangeUnit, rangeHeader, getBetween, getLast, getter);
    }

    public static <T> ResponseEntity<List<T>> handleRequest(
            String rangeUnit, String rangeHeader, Class<T> type, GetTypedBetweenFunction<Integer, T> getBetween,
            GetTypedLastFunction<T> getLast) {
        RangeUtil.requiresValidRangeUnit(rangeUnit);
        Objects.requireNonNull(rangeHeader);
        Objects.requireNonNull(getBetween);
        Objects.requireNonNull(getLast);
        return handleRequest(rangeUnit, rangeHeader, getBetweenOfType(getBetween, type), getLastOfType(getLast, type));
    }

    public static <T> ResponseEntity<List<T>> handleRequest(
            String rangeUnit, String rangeHeader, BiFunction<Integer, Integer, List<T>> getBetween,
            Function<Integer, List<T>> getLast) {
        RangeUtil.requiresValidRangeUnit(rangeUnit);
        Objects.requireNonNull(rangeHeader);
        Objects.requireNonNull(getBetween);
        Objects.requireNonNull(getLast);
        return RangeUtil.handleRangeRequest(rangeUnit, rangeHeader, getBetween, getLast, null);
    }

    public static <N,T> BiFunction<N, N, List<T>> getBetweenOfType(
            GetTypedBetweenFunction<N, T> getBetween, Class<T> type) {
        Objects.requireNonNull(getBetween);
        Objects.requireNonNull(type);
        return (minId, maxId) -> getBetween.apply(minId, maxId, type);
    }

    public static <T> BiFunction<Integer, Integer, List<T>> getBetweenUsingLong(
            BiFunction<Long, Long, List<T>> getBetween) {
        Objects.requireNonNull(getBetween);
        return (min, max) -> getBetween.apply(min.longValue(), max.longValue());
    }

    public static <T> Function<Integer, List<T>> getLastOfType(
            GetTypedLastFunction<T> getLast, Class<T> type) {
        Objects.requireNonNull(getLast);
        Objects.requireNonNull(type);
        return last -> getLast.apply(last, type);
    }

    public static <T> Function<T, Integer> integerGetter(Function<T, Long> getter) {
        Objects.requireNonNull(getter);
        return t -> Math.toIntExact(getter.apply(t));
    }

    @FunctionalInterface
    public interface GetTypedBetweenFunction<N, T> {
        List<T> apply(N minId, N maxId, Class<T> type);
    }

    @FunctionalInterface
    public interface GetTypedLastFunction<T> {
        List<T> apply(Integer last, Class<T> type);
    }
}
