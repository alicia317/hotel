/*
 * Copyright (C) 2024 - present Juergen Zimmermann, Hochschule Karlsruhe
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.hotel.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service-Klasse, um Benutzernamen und Rollen aus einem JWT von Keycloak zu extrahieren.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Service
@Slf4j
@SuppressWarnings("java:S5852")
public class JwtService {
    /**
     * Zu einem gegebenen JWT wird der zugehörige Username gesucht.
     *
     * @param jwt JWT für Security
     * @return Der gesuchte Username oder null
     */
    public String getUsername(final Jwt jwt) {
        log.debug("getUsername");
        if (jwt == null) {
            throw new UsernameNotFoundException("JWT == null");
        }
        final var username = (String) jwt.getClaims().get("preferred_username");
        log.debug("getUsername: username={}", username);
        return username;
    }

    /**
     * Zu einem gegebenen JWT werden die zugehörigen Rollen gesucht.
     *
     * @param jwt JWT für Security
     * @return Die gesuchten Rollen oder die leere Liste
     */
    public List<Rolle> getRollen(final Jwt jwt) {
        @SuppressWarnings("unchecked")
        final var realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
        final var rollenStr = realmAccess.get("roles");
        log.trace("getRollen: rollenStr={}", rollenStr);
        return rollenStr
            .stream()
            .map(Rolle::of)
            .filter(Objects::nonNull)
            .toList();
    }
}
