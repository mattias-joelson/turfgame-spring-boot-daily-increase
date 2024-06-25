package org.joelson.turf.dailyinc.api;

public record JsonError(String type, String title, int status, String detail, String instance) {
}
