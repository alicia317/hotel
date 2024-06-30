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
package com.acme.hotel.repository;

import com.acme.hotel.entity.Hotel;
import com.acme.hotel.entity.Hotel_;
import com.acme.hotel.entity.Adresse_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Singleton-Klasse, um Specifications für Queries in Spring Data JPA zu bauen.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Component
@Slf4j
@SuppressWarnings({"LambdaParameterName", "IllegalIdentifierName"})
public class SpecificationBuilder {
    /**
     * Specification für eine Query mit Spring Data bauen.
     *
     * @param queryParams als MultiValueMap
     * @return Specification für eine Query mit Spring Data
     */
    public Optional<Specification<Hotel>> build(final Map<String, ? extends List<String>> queryParams) {
        log.debug("build: queryParams={}", queryParams);

        if (queryParams.isEmpty()) {
            // keine Suchkriterien
            return Optional.empty();
        }

        final var specs = queryParams
            .entrySet()
            .stream()
            .map(this::toSpecification)
            .toList();

        if (specs.isEmpty() || specs.contains(null)) {
            return Optional.empty();
        }

        return Optional.of(Specification.allOf(specs));
    }

    @SuppressWarnings("CyclomaticComplexity")
    private Specification<Hotel> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
        log.trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();

        if (values == null || values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "hotelname" -> hotelname(value);
            case "plz" -> plz(value);
            case "ort" -> ort(value);
            default -> null;
        };
    }

    private Specification<Hotel> hotelname(final String teil) {
        // root ist jakarta.persistence.criteria.Root<Hotel>
        // query ist jakarta.persistence.criteria.CriteriaQuery<Hotel>
        // builder ist jakarta.persistence.criteria.CriteriaBuilder
        // https://www.logicbig.com/tutorials/java-ee-tutorial/jpa/meta-model.html
        return (root, _, builder) -> builder.like(
            builder.lower(root.get(Hotel_.hotelname)),
            builder.lower(builder.literal("%" + teil + '%'))
        );
    }

    private Specification<Hotel> plz(final String prefix) {
        return (root, _, builder) -> builder.like(root.get(Hotel_.adresse).get(Adresse_.plz), prefix + '%');
    }

    private Specification<Hotel> ort(final String prefix) {
        return (root, _, builder) -> builder.like(
            builder.lower(root.get(Hotel_.adresse).get(Adresse_.ort)),
            builder.lower(builder.literal(prefix + '%'))
        );
    }
}
