/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.hotel.security;

import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Singleton für verfügbare Rollen als Strings für das Spring-Interface GrantedAuthority.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@RequiredArgsConstructor
public enum Rolle {
    /**
     * Die Rolle ADMIN.
     */
    ADMIN("ADMIN"),

    /**
     * Die Rolle USER.
     */
    USER("USER");
    private final String value;

    /**
     * Zu einem String die Rolle als Enum ermitteln.
     *
     * @param str String einer Rolle
     * @return Rolle als Enum oder null
     */
    public static Rolle of(final String str) {
        return Stream.of(values())
            .filter(rolle -> rolle.name().equalsIgnoreCase(str))
            .findFirst()
            .orElse(null);
    }
}
