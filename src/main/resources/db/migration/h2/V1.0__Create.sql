CREATE TABLE IF NOT EXISTS adresse (
    id        UUID PRIMARY KEY,
    plz       CHAR(5) NOT NULL CHECK (plz ~ '\d{5}'),
    ort       VARCHAR(40) NOT NULL
    );
CREATE INDEX IF NOT EXISTS adresse_plz_idx ON adresse(plz);

CREATE TABLE IF NOT EXISTS hotel (
    id            UUID PRIMARY KEY,
    -- http://www.h2database.com/html/datatypes.html#integer_type
    version       INTEGER NOT NULL DEFAULT 0,
    hotelname      VARCHAR(40) NOT NULL,
    adresse_id    UUID NOT NULL UNIQUE REFERENCES adresse(id),
    username      VARCHAR(20) NOT NULL,
    -- http://www.h2database.com/html/datatypes.html#timestamp_type
    erzeugt       TIMESTAMP NOT NULL,
    aktualisiert  TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS zimmer (
    id        UUID PRIMARY KEY,
    -- http://www.h2database.com/html/datatypes.html#numeric_type
    -- 10 Stellen, davon 2 Nachkommastellen
    zimmernummer INTEGER NOT NULL,
    istBelegt    BOOLEAN,
    anzahlBetten INTEGER,
    hotel_id  UUID REFERENCES hotel,
    idx       INTEGER NOT NULL DEFAULT 0
    );
CREATE INDEX IF NOT EXISTS zimmer_hotel_id_idx ON zimmer(hotel_id);
