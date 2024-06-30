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

//import com.c4_soft.springaddons.security.oidc.starter.synchronised.resourceserver.ResourceServerExpressionInterceptUrlRegistryPostProcessor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import java.util.Map;
import static com.acme.hotel.rest.HotelGetController.REST_PATH;
import static com.acme.hotel.security.AuthController.AUTH_PATH;
import static com.acme.hotel.security.Rolle.ADMIN;
import static com.acme.hotel.security.Rolle.USER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

// https://github.com/spring-projects/spring-security/tree/master/samples

/**
 * Security-Konfiguration.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@SuppressWarnings("TrailingComment")
public interface SecurityConfig {
    /**
     * Bean-Methode zur Integration von Spring Security mit Keycloak.
     *
     * @return Post-Prozessor für Spring Security zur Integration mit Keycloak
     */
//    @Bean
//    default ResourceServerExpressionInterceptUrlRegistryPostProcessor authorizePostProcessor() {
//        return registry -> registry
//            .requestMatchers(OPTIONS, "/rest/**").permitAll()
//            .requestMatchers("/rest/**").authenticated()
//            .anyRequest().authenticated();
//    }

    /**
     * Bean-Definition, um den Zugriffsschutz an der REST-Schnittstelle zu konfigurieren,
     * d.h. vor Anwendung von @PreAuthorize.
     *
     * @param http Injiziertes Objekt von HttpSecurity als Ausgangspunkt für die Konfiguration.
     * @param authenticationConverter Injiziertes Objekt von Converter für die Anpassung an Keycloak
     * @return Objekt von SecurityFilterChain
     * @throws Exception Wegen HttpSecurity.authorizeHttpRequests()
     */
    // https://github.com/spring-projects/spring-security-samples/blob/main/servlet/java-configuration/...
    // ...authentication/preauth/src/main/java/example/SecurityConfiguration.java
    @Bean
    @SuppressWarnings("LambdaBodyLength")
    default SecurityFilterChain securityFilterChain(
        final HttpSecurity http,
        final Converter<Jwt, AbstractAuthenticationToken> authenticationConverter
    ) throws Exception {
        return http
            .authorizeHttpRequests(authorize -> {
                final var restPathHotelId = REST_PATH + "/*";
                authorize
                    .requestMatchers(OPTIONS, REST_PATH + "/**").permitAll()
                    .requestMatchers(GET, AUTH_PATH + "/me").hasAnyRole(ADMIN.name(), USER.name())

                    // https://spring.io/blog/2020/06/30/url-matching-with-pathpattern-in-spring-mvc
                    // https://docs.spring.io/spring-security/reference/current/servlet/integrations/mvc.html
                    .requestMatchers(GET, REST_PATH).hasRole(ADMIN.name())
                    .requestMatchers(
                        GET,
                        REST_PATH + "/*",
                        "/swagger-ui.html"
                    ).hasRole(ADMIN.name())
                    .requestMatchers(GET, restPathHotelId).hasAnyRole(ADMIN.name(), USER.name())
                    .requestMatchers(PUT, restPathHotelId).hasRole(ADMIN.name())
                    .requestMatchers(PATCH, restPathHotelId).hasRole(ADMIN.name())
                    .requestMatchers(DELETE, restPathHotelId).hasRole(ADMIN.name())

                    .requestMatchers(POST, "/dev/db_populate").hasRole(ADMIN.name())

                    .requestMatchers(POST, REST_PATH, "/graphql", AUTH_PATH + "/login").permitAll()

                    .requestMatchers(
                        // Actuator: Health mit Liveness und Readiness fuer Kubernetes
                        EndpointRequest.to(HealthEndpoint.class),
                        // Actuator: Prometheus fuer Monitoring
                        EndpointRequest.to(PrometheusScrapeEndpoint.class)
                    ).permitAll()
                    // OpenAPI bzw. Swagger UI und GraphiQL
                    .requestMatchers(GET, "/v3/api-docs.yaml", "/v3/api-docs", "/graphiql").permitAll()
                    .requestMatchers("/error", "/error/**").permitAll()

                    // https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html
                    // https://marco.dev/spring-boot-h2-error
                    // .requestMatchers(toH2Console()).permitAll()

                    .anyRequest().authenticated();
            })

            .oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(authenticationConverter))
            )

            // Spring Security erzeugt keine HttpSession und verwendet keine fuer SecurityContext
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable) // NOSONAR
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
            .build();
    }

    /**
     * Bean-Methode für die Überprüfung, ob ein Passwort ein bekanntes ("gehacktes") Passwort ist.
     *
     * @return "Checker-Objekt" für die Überprüfung, ob ein Passwort ein bekanntes ("gehacktes") Passwort ist
     */
    @Bean
    default CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    /**
     * Bean-Definition, um den Verschlüsselungsalgorithmus für Passwörter bereitzustellen.
     * Es wird Argon2id statt bcrypt (Default-Algorithmus von Spring Security) verwendet.
     *
     * @return Objekt für die Verschlüsselung von Passwörtern.
     */
    @Bean
    default PasswordEncoder passwordEncoder() {
        // https://foojay.io/today/how-to-do-password-hashing-in-java-applications-the-right-way
        // Salt für Argon2.
        final var saltLength = 32; // default: 16
        // Hash-Länge für Argon2.
        final var hashLength = 64; // default: 32
        // Parallelität für Argon2.
        final var parallelism = 1; // default: 1 (Bouncy Castle kann keine Parallelitaet)
        // Anzahl Bits für "Memory Consumption" bei Argon2.
        final var numberOfBits = 14;
        // "Memory Consumption" in KBytes bei Argon2.
        final var memoryConsumptionKbytes = 1 << numberOfBits; // default: 2^14 KByte = 16 MiB ("Memory Cost Parameter")
        // Anzahl Iterationen bei Argon2.
        final var iterations = 3; // default: 3

        // https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
        // https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Password_Storage_Cheat_Sheet.md
        // https://www.rfc-editor.org/rfc/rfc9106.html
        final var idForEncode = "argon2id";
        final Map<String, PasswordEncoder> encoders = Map.of(
            idForEncode,
            new Argon2PasswordEncoder(
                saltLength,
                hashLength,
                parallelism,
                memoryConsumptionKbytes,
                iterations
            )
        );
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}
