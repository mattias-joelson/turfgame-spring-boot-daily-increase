package org.joelson.turf.dailyinc.api;

import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class RangeUtil {

    private static final int INVALID = -1;

    private RangeUtil() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated.");
    }

    public static <T> ResponseEntity<List<T>> handleRangeRequest(
            String rangeUnit, String rangeHeader, BiFunction<Integer, Integer, List<T>> getBetween,
            Function<Integer, List<T>> getLast, Function<T, Integer> getter) {
        try {
            RangeRequest rangeRequest = parseRange(rangeHeader);
            if (!rangeUnit.equals(rangeRequest.getRangeUnit())) {
                return RangeResponseUtil.createRequestRangeNotSatisfiableResponse(rangeUnit);
            }
            List<T> list = rangeRequest.getRange(getBetween, getLast);
            if (getter != null) {
                if (!list.isEmpty()) {
                    return RangeResponseUtil.createPartialContentResponse(rangeUnit,
                            list, getter.apply(list.getFirst()), getter.apply(list.getLast()));
                } else {
                    return RangeResponseUtil.createPartialContentResponse(rangeUnit,
                            list, 0, 0);
                }
            } else {
                int startPos = (rangeRequest.firstPos != INVALID) ? rangeRequest.firstPos : 1000000000;
                return RangeResponseUtil.createPartialContentResponse(rangeUnit, list, startPos,
                        startPos + list.size() - 1);
            }
        } catch (ParseException e) {
            return RangeResponseUtil.createRequestRangeNotSatisfiableResponse(rangeUnit);
        }
    }

    static RangeRequest parseRange(String rangeValue) throws ParseException {
        if (rangeValue == null) {
            throw new ParseException("rangeValue is null", -1);
        }
        int eqIndex = rangeValue.indexOf('=');
        if (eqIndex < 0) {
            throw new ParseException("missing =", 0);
        }
        if (rangeValue.indexOf('=', eqIndex + 1) > eqIndex) {
            throw new ParseException("second =", rangeValue.indexOf('=', eqIndex + 1));
        }
        String rangeUnit;
        try {
            rangeUnit = requiresValidRangeUnit(rangeValue.substring(0, eqIndex));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        int hyphenIndex = rangeValue.indexOf('-', eqIndex + 1);
        if (hyphenIndex < eqIndex + 1) {
            throw new ParseException("missing -", eqIndex + 1);
        }
        if (rangeValue.indexOf('-', hyphenIndex + 1) > hyphenIndex) {
            throw new ParseException("second -", rangeValue.indexOf('-', hyphenIndex + 1));
        }
        int firstPos = INVALID;
        if (hyphenIndex - eqIndex - 1 > 0) {
            try {
                firstPos = Integer.parseInt(rangeValue.substring(eqIndex + 1, hyphenIndex));
            } catch (NumberFormatException e) {
                throw new ParseException("invalid firstPos", eqIndex + 1);
            }
        }
        int lastPos = INVALID;
        if (hyphenIndex < rangeValue.length() - 1) {
            try {
                lastPos = Integer.parseInt(rangeValue.substring(hyphenIndex + 1));
            } catch (NumberFormatException e) {
                throw new ParseException("invalid lastPos", hyphenIndex + 1);
            }
        }
        try {
            return new RangeRequest(rangeUnit, firstPos, lastPos);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    public static String requiresValidRangeUnit(String rangeUnit) {
        if (rangeUnit != null && matchesRangeUnitPattern(rangeUnit)) {
            return rangeUnit;
        }
        if (rangeUnit == null) {
            throw new IllegalArgumentException("rangeUnit is null");
        }
        throw new IllegalArgumentException(String.format("Invalid rangeUnit=\"%s\"", rangeUnit));
    }

    private static boolean matchesRangeUnitPattern(String rangeUnit) {
        return RangeUnitPatternHolder.RANGE_UNIT_PATTERN.matcher(rangeUnit).matches();
    }

    private static int requiresNotLessThanInvalid(int value, String name) {
        if (value >= INVALID) {
            return value;
        }
        throw new IllegalArgumentException(String.format("%s=%d < -1", name, value));
    }

    public static String getRange(String rangeUnit, int firstPos) {
        RangeRequest request = new RangeRequest(rangeUnit, firstPos, INVALID);
        return String.format("%s=%d-", request.rangeUnit, request.firstPos);
    }

    public static String getRange(String rangeUnit, int firstPos, int lastPos) {
        RangeRequest request = new RangeRequest(rangeUnit, firstPos, lastPos);
        return String.format("%s=%d-%d", request.rangeUnit, request.firstPos, request.lastPos);
    }

    public static String getRangeSuffix(String rangeUnit, int last) {
        RangeRequest request = new RangeRequest(rangeUnit, INVALID, last);
        return String.format("%s=-%d", request.rangeUnit, request.lastPos);
    }

    public static String getAcceptRanges(String rangeUnit) {
        return requiresValidRangeUnit(rangeUnit);
    }

    public static String getContentRange(String rangeUnit, int firstPos, int lastPos) {
        requiresValidRangeUnit(rangeUnit);
        if (firstPos < 0) {
            throw new IllegalArgumentException(String.format("firstPos=%d can not be negative", firstPos));
        }
        if (lastPos < firstPos) {
            throw new IllegalArgumentException(String.format("firstPos=%d > lastPos=%d", firstPos, lastPos));
        }
        return String.format("%s %d-%d/*", rangeUnit, firstPos, lastPos);
    }

    public static String getUnsatisfiableContentRange(String rangeUnit) {
        return String.format("%s */*", requiresValidRangeUnit(rangeUnit));
    }

    static class RangeRequest {

        private final String rangeUnit;
        private final int firstPos;
        private final int lastPos;

        private RangeRequest(String rangeUnit, int firstPos, int lastPos) {
            this.rangeUnit = requiresValidRangeUnit(rangeUnit);
            this.firstPos = requiresNotLessThanInvalid(firstPos, "firstPos");
            this.lastPos = requiresNotLessThanInvalid(lastPos, "lastPos");
            if (firstPos == INVALID && lastPos == INVALID) {
                throw new IllegalArgumentException(
                        String.format("firstPos=%d invalid and suffixLength=%d invalid", firstPos, lastPos));
            }
            if (firstPos != INVALID && lastPos != INVALID && firstPos > lastPos) {
                throw new IllegalArgumentException(String.format("firstPos=%d > lastPos=%d", firstPos, lastPos));
            }
        }

        String getRangeUnit() {
            return rangeUnit;
        }

        <T> List<T> getRange(
                BiFunction<Integer, Integer, List<T>> getBetween, Function<Integer, List<T>> getLast) {
            if (firstPos != INVALID) {
                if (lastPos != INVALID) {
                    return getBetween.apply(firstPos, lastPos);
                } else {
                    return getBetween.apply(firstPos, Integer.MAX_VALUE);
                }
            } else {
                return getLast.apply(lastPos);
            }
        }
    }

    private static final class RangeUnitPatternHolder {
        private static final Pattern RANGE_UNIT_PATTERN = Pattern.compile("[!#$%&'*+.^_`|~0-9A-Za-z-]+");
    }
}
