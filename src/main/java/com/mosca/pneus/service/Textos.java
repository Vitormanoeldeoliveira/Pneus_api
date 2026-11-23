package com.mosca.pneus.service;

public final class Textos {

    private Textos() {
    }

    public static String upper(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    public static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static String q(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
