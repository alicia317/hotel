package com.acme.hotel.rest;

import com.acme.hotel.entity.Adresse;
import com.acme.hotel.entity.Hotel;
import com.acme.hotel.entity.Zimmer;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

/**
 * Mapper zwischen Entity-Klassen.
 *
 * @author Alicia Schleise
 */
@Mapper(nullValueIterableMappingStrategy = RETURN_DEFAULT, componentModel = "spring")
@AnnotateWith(ExcludeFromJacocoGeneratedReport.class)
interface HotelMapper {
    /**
     * Ein DTO-Objekt von HotelDTO.
     *
     * @param dto DTO-Objekt für HotelDTO ohne ID
     * @return Konvertiertes Hotel-Objekt mit null als ID
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "erzeugt", ignore = true)
    @Mapping(target = "aktualisiert", ignore = true)
    Hotel toHotel(HotelDTO dto);

    /**
     * Ein DTO-Objekt von AdresseDTO.
     *
     * @param dto DTO-Objekt für AdresseDTO
     * @return Konvertiertes Adresse-Objekt
     */
    @Mapping(target = "id", ignore = true)
    Adresse toAdresse(AdresseDTO dto);

    /**
     * Ein DTO-Objekt von ZimmerDTO.
     *
     * @param dto DTO-Objekt für ZimmerDTO
     * @return Konvertiertes Zimmer-Objekt
     */
    @Mapping(target = "id", ignore = true)
    Zimmer toZimmer(ZimmerDTO dto);
}
