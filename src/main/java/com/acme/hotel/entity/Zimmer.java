package com.acme.hotel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

/**
 * Entity Zimmer, für die Daten eines Zimmers.
 *
 * @author Alicia Schleise
 */
@Entity
@Table(name = "zimmer")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Zimmer {

    @Id
    @GeneratedValue
    // Oracle: https://in.relation.to/2022/05/12/orm-uuid-mapping
    // @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
    private UUID id;

    /**
     * Die Zimmernummer die ein Zimmer hat.
     */
    @NotBlank
    private int zimmernummer;

    /**
     * ist_belegt als Abfrage, ob das Zimmer belegt ist.
     */
    private Boolean istBelegt;

    /**
     * Die Anzahl an Betten die in einem Zimmer stehen.
     */
    @NotBlank
    private int anzahlBetten;
}
