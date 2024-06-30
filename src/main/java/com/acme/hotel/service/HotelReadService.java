package com.acme.hotel.service;

import com.acme.hotel.entity.Hotel;
import com.acme.hotel.repository.HotelRepository;
import com.acme.hotel.repository.SpecificationBuilder;
import com.acme.hotel.security.Rolle;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.acme.hotel.security.Rolle.ADMIN;

/**
 * Anwendungslogik für Hotels.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HotelReadService {

    private final HotelRepository repo;
    private final SpecificationBuilder specificationBuilder;

    /**
     * Ein Hotel anhand seiner ID suchen.
     *
     * @param id Die Id des gesuchten Hotels
     * @param username UserDetails-Objekt
     * @param rollen Rollen als Liste von Enums
     * @param fetchVorhandeneZimmer true, falls die Zimmer mitgeladen werden sollen
     * @return Das gefundene Hotel
     * @throws NotFoundException Falls kein Hotel gefunden wurde
     * @throws AccessForbiddenException Falls die erforderlichen Rollen nicht gegeben sind
     */
    @Observed(name = "find-by-id")
    public @NonNull Hotel findById(final UUID id, final String username, final List<Rolle> rollen, final boolean fetchVorhandeneZimmer) {
        log.debug("findById: id={}, username={}, rollen={}", id, username, rollen);

        final var hotelOptional = fetchVorhandeneZimmer ? repo.findByIdFetchVorhandeneZimmer(id) : repo.findById(id);
        final var hotel = hotelOptional.orElse(null);
        log.trace("findById: hotel={}", hotel);

        // beide find()-Methoden innerhalb von observe() liefern ein Optional
        if (hotel != null && hotel.getUsername().contentEquals(username)) {
            // eigene hoteldaten
            return hotel;
        }

//        final var rollen = user
//            .getAuthorities()
//            .stream()
//            .map(GrantedAuthority::getAuthority)
//            .map(str -> str.substring(Rolle.ROLE_PREFIX.length()))
//            .map(Rolle::valueOf)
//            .toList();
        if (!rollen.contains(ADMIN)) {
            // nicht admin, aber keine eigenen (oder keine) Hoteldaten
            throw new AccessForbiddenException(rollen);
        }

        // admin: Hoteldaten evtl. nicht gefunden
        if (hotel == null) {
            throw new NotFoundException(id);
        }
        log.debug("findById: hotel={}, vorhandeneZimmer={}", hotel, fetchVorhandeneZimmer ? hotel.getVorhandeneZimmer() : "N/A");
        return hotel;
    }

    /**
     * Hotels anhand von Suchkriterien als Collection suchen.
     *
     * @param suchkriterien Die Suchkriterien
     * @return Die gefundenen Hotels oder eine leere Liste
     * @throws NotFoundException Falls keine Hotels gefunden wurden
     */
    public @NonNull Collection<Hotel> find(@NonNull final Map<String, List<String>> suchkriterien) {
        log.debug("find: suchkriterien={}", suchkriterien);

        if (suchkriterien.isEmpty()) {
            return repo.findAll();
        }

        if (suchkriterien.size() == 1) {
            final var hotelnamen = suchkriterien.get("hotelname");
            if (hotelnamen != null && hotelnamen.size() == 1) {
                return findByHotelname(hotelnamen.getFirst(), suchkriterien);
            }
        }

        final var specification = specificationBuilder
            .build(suchkriterien)
            .orElseThrow(() -> new NotFoundException(suchkriterien));
        final var hotels = repo.findAll(specification);
        if (hotels.isEmpty()) {
            throw new NotFoundException(suchkriterien);
        }
        log.debug("find: {}", hotels);
        return hotels;

        //final var hotels = repo.find(suchkriterien);
        //if (hotels.isEmpty()) {
        //    throw new NotFoundException(suchkriterien);
        //}
        //log.debug("find: {}", hotels);
        //return hotels;
    }

    private List<Hotel> findByHotelname(final String hotelname, final Map<String, List<String>> suchkriterien) {
        log.trace("findByHotelname: {}", hotelname);
        final var hotels = repo.findByHotelname(hotelname);
        if (hotels.isEmpty()) {
            throw new NotFoundException(suchkriterien);
        }
        log.debug("findByhotelname: {}", hotels);
        return (List<Hotel>) hotels;
    }
}
