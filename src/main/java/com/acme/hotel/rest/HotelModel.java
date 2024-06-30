package com.acme.hotel.rest;

import com.acme.hotel.entity.Adresse;
import com.acme.hotel.entity.Hotel;
import com.acme.hotel.entity.Zimmer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Model-Klasse für Spring HATEOAS. @lombok. Data fasst die Annotationen @ToString, @EqualsAndHashCode, @Getter, @Setter
 * und @RequiredArgsConstructor zusammen.
 *
 * @author Alicia Schleise
 */
@JsonPropertyOrder({
    "hotelname", "vorhandeneZimmer", "adresse"
})
@Relation(collectionRelation = "hotels", itemRelation = "hotel")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
class HotelModel extends RepresentationModel<HotelModel> {

    private final String hotelname;
    private final List<Zimmer> vorhandeneZimmer;
    private final Adresse adresse;

    HotelModel(final Hotel hotel) {
        hotelname = hotel.getHotelname();
        vorhandeneZimmer = hotel.getVorhandeneZimmer();
        adresse = hotel.getAdresse();
    }
}
