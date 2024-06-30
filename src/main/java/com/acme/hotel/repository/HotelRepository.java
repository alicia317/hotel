package com.acme.hotel.repository;

import com.acme.hotel.entity.Hotel;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import static com.acme.hotel.entity.Hotel.ADRESSE_GRAPH;
import static com.acme.hotel.entity.Hotel.ADRESSE_VORHANDENEZIMMER_GRAPH;

/**
 * Repository für den DB-Zugriff bei den Hotels.
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {

    @EntityGraph(ADRESSE_GRAPH)
    @NonNull
    @Override
    List<Hotel> findAll();

    @EntityGraph(ADRESSE_GRAPH)
    @NonNull
    @Override
    List<Hotel> findAll(@NonNull Specification<Hotel> spec);

    @EntityGraph(ADRESSE_GRAPH)
    @NonNull
    @Override
    Optional<Hotel> findById(@NonNull UUID id);

    /**
     * Hotel einschließlich Zimmer anhand der ID suchen.
     *
     * @param id Hotel ID
     * @return Gefundenes Hotel
     */
    @Query("""
        SELECT DISTINCT h
        FROM     Hotel h
        WHERE    h.id = :id
        """)
    @EntityGraph(ADRESSE_VORHANDENEZIMMER_GRAPH)
    @NonNull
    Optional<Hotel> findByIdFetchVorhandeneZimmer(UUID id);

    /**
     * Hotel anhand des Nachnamens suchen.
     *
     * @param hotelname Der (Teil-) Hotelname der gesuchten Hotels
     * @return Die gefundenen Hotels oder eine leere Collection
     */
    @Query("""
        SELECT   h
        FROM     Hotel h
        WHERE    lower(h.hotelname) LIKE concat('%', lower(:hotelname), '%')
        ORDER BY h.id
        """)
    @EntityGraph(ADRESSE_GRAPH)
    Collection<Hotel> findByHotelname(CharSequence hotelname);

}
