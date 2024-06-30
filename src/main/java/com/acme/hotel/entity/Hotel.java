package com.acme.hotel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Entity Hotel, für die Daten eines Hotels.
 *
 * @author Alicia Schleise
 */
@Entity
@Table(name = "hotel")
@NamedEntityGraph(name = Hotel.ADRESSE_GRAPH, attributeNodes = @NamedAttributeNode("adresse"))
@NamedEntityGraph(name = Hotel.ADRESSE_VORHANDENEZIMMER_GRAPH, attributeNodes = {
    @NamedAttributeNode("adresse"), @NamedAttributeNode("vorhandeneZimmer")
})
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@Builder
public class Hotel {

    /**
     * NamedEntityGraph für das Attribut "adresse".
     */
    public static final String ADRESSE_GRAPH = "Hotel.adresse";

    /**
     * NamedEntityGraph für die Attribute "adresse" und "vorhandeneZimmer".
     */
    public static final String ADRESSE_VORHANDENEZIMMER_GRAPH = "Hotel.adresseVorhandenezimmer";

    /**
     * Die eindeutige ID eines Hotels.
     */
    @Id
    // https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html...
    // ...#identifiers-generators-uuid
    // https://in.relation.to/2022/05/12/orm-uuid-mapping
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    private int version;

    /**
     * Der Name den ein Hotel hat.
     */
    @NotNull
    private String hotelname;

    /**
     * Die Adresse des Hotels.
     */
    @OneToOne(optional = false, cascade = {PERSIST, REMOVE}, fetch = LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Adresse adresse;

    /**
     * Die Liste an vorhandenen Zimmern des Hotels.
     */
    // Default: fetch=LAZY
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "hotel_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<Zimmer> vorhandeneZimmer;

    private String username;

    // https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html...
    // ...#mapping-generated-CreationTimestamp
    @CreationTimestamp
    private LocalDateTime erzeugt;

    // https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html...
    // ...#mapping-generated-UpdateTimestamp
    @UpdateTimestamp
    private LocalDateTime aktualisiert;

    /**
     * Hoteldaten überschreiben.
     *
     * @param hotel Neue Hoteldaten.
     */
    public void set(final Hotel hotel) {
        hotelname = hotel.hotelname;
    }
}
