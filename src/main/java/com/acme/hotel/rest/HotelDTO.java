package com.acme.hotel.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.List;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Hotels. Beim Lesen wird die Klasse HotelModel für die Ausgabe
 * verwendet.
 *
 * @param hotelname Gültiger Name eines Hotels
 * @param adresse Die Adresse eines Hotels.
 * @param vorhandeneZimmer Die Zimmer die ein Hotel besitzt.
 * @param username Benutzername
 * @param password Passwort
 */
@Builder
record HotelDTO(

    String hotelname,

    @Valid
    // https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single#_requesting_groups
    @NotNull(groups = OnCreate.class)
    AdresseDTO adresse,

    List<ZimmerDTO> vorhandeneZimmer,

    String username,
    String password
) {
    /**
     * Marker-Interface f&uuml;r Jakarta Validation: zus&auml;tzliche Validierung beim Neuanlegen.
     */
    interface OnCreate { }
}
