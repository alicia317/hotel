package com.acme.hotel.service;

import com.acme.hotel.entity.Hotel;
import com.acme.hotel.mail.Mailer;
import com.acme.hotel.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Anwendungslogik für Hotels.
 *
 * @author Alicia Schleise
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HotelWriteService {

    private final HotelRepository repo;
    private final Mailer mailer;

    /**
     * Ein neues Hotel anlegen.
     *
     * @param hotel Das Objekt des neu anzulegenden Hotel.
     * @return Das neu angelegte Hotel mit generierter ID
     */
    @Transactional
    public Hotel create(final Hotel hotel) {
        log.debug("create: hotel={}", hotel);
        log.debug("create: adresse={}", hotel.getAdresse());
        log.debug("create: vorhandeneZimmer={}", hotel.getVorhandeneZimmer());

        hotel.setUsername("user");

        final var hotelDB = repo.save(hotel);

        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());
        mailer.send(hotelDB);

        log.debug("create: hotelDB={}", hotelDB);
        return hotelDB;
    }

    /**
     * Ein vorhandenes Hotel aktualisieren.
     *
     * @param hotel Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Hotels
     * @param version Die erforderliche Version
     * @throws NotFoundException Kein Hotel zur ID vorhanden.
     */
    @Transactional
    public Hotel update(final Hotel hotel, final UUID id, final int version) {
        log.debug("update: hotel={}", hotel);
        log.debug("update: id={}, version={}", id, version);

        var hotelDb = repo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(id));
        log.trace("update: version={}, hotelDb={}", version, hotelDb);
        if (version != hotelDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        // Zu ueberschreibende Werte uebernehmen
        hotelDb.set(hotel);
        hotelDb = repo.save(hotelDb);

        log.debug("update: {}", hotelDb);
        return hotelDb;
    }
}
