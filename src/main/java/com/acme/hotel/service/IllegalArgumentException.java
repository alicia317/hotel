package com.acme.hotel.service;

import lombok.Getter;
import java.util.List;
import java.util.Map;

/**
 * RuntimeException, falls kein Gast gefunden wurde.
 */
@Getter
public final class IllegalArgumentException extends RuntimeException {
    /**
     * Nicht-vorhandene ID.
     */
    private final String key;

    /**
     * Suchkriterien, zu denen nichts gefunden wurde.
     */
    private final Map<String, List<String>> suchkriterien;

    /**
     * kp.
     *
     * @param key kp.
     */
    public IllegalArgumentException(final String key) {
        super(STR."Ungueltiger Schluessel: \{key}");
        suchkriterien = null;
        this.key = key;
    }

    /**
     * kp.
     *
     * @param suchkriterien kp.
     */
    IllegalArgumentException(final Map<String, List<String>> suchkriterien) {
        super(STR."Kein Hotel zum Kriterium -\{suchkriterien}- gefunden");
        this.key = null;
        this.suchkriterien = suchkriterien;
    }

    /**
     * kp.
     */
    public IllegalArgumentException() {
        super("Kein Hotel gefunden.");
        this.key = null;
        suchkriterien = null;
    }
}
