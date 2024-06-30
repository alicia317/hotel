package com.acme.hotel.rest;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen HotelsS. Beim Lesen wird die Klasse KundeModel für die Ausgabe
 * verwendet.
 *
 * @param zimmernummer Die Zimmernummer
 * @param istBelegt Ob das Zimmer belegt ist.
 * @param anzahlBetten Die Anzahl an Betten in dem Zimmer.
 */
record ZimmerDTO(

    int zimmernummer,
    boolean istBelegt,
    int anzahlBetten
) {
}
