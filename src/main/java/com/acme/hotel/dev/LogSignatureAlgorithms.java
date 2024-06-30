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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.hotel.dev;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import static org.springframework.context.annotation.Bean.Bootstrap.BACKGROUND;

/**
 * Beim ApplicationReadyEvent werden Informationen für die Entwickler/innen im Hinblick auf Security (-Algorithmen)
 * protokolliert. Da es viele Algorithmen gibt und die Ausgabe lang wird, wird diese Funktionalität nur mit dem
 * Profile logSecurity und nicht allgemein verwendet.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
public interface LogSignatureAlgorithms {
    /**
     * Bean-Definition, um einen Listener bereitzustellen, damit die im JDK vorhandenen Signature-Algorithmen
     * aufgelistet werden.
     *
     * @return Listener für die Ausgabe der Signature-Algorithmen
     */
    @Bean(bootstrap = BACKGROUND)
    @Profile("logSecurity")
    @SuppressWarnings("LambdaBodyLength")
    default ApplicationListener<ApplicationReadyEvent> logSignatureAlgorithms() {
        final var log = LoggerFactory.getLogger(LogSignatureAlgorithms.class);
        return _ -> Arrays
                .stream(Security.getProviders())
                .forEach(provider -> logSignatureAlgorithms(provider, log));
    }

    private void logSignatureAlgorithms(final Provider provider, final Logger log) {
        provider
            .getServices()
            .forEach(service -> {
                if ("Signature".contentEquals(service.getType())) {
                    log.debug("{}", service.getAlgorithm());
                }
            });
    }
}
