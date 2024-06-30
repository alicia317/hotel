package com.acme.hotel.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

/**
 * Entity Adresse, für die daten einer Adresse.
 *
 * @author Alicia Schleise
 */
@Entity
@Table(name = "adresse")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Adresse {

    @Id
    @GeneratedValue
    // Oracle: https://in.relation.to/2022/05/12/orm-uuid-mapping
    // @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
    private UUID id;

    /**
     * Die Postleitzahl für die Adresse.
     * plz - die Postleitzahl als String
     */
    private String plz;

    /**
     * Der Ort für die Adresse.
     * ort - der Ort als String
     */
    private String ort;
}
