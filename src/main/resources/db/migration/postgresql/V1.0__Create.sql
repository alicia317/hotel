CREATE TABLE IF NOT EXISTS login (
    id       uuid PRIMARY KEY USING INDEX TABLESPACE hotelspace,
    username varchar(20) NOT NULL UNIQUE USING INDEX TABLESPACE hotelspace,
    password varchar(180) NOT NULL,
    rollen   varchar(32)
    ) TABLESPACE hotelspace;

CREATE TABLE IF NOT EXISTS adresse (
    id        UUID PRIMARY KEY USING INDEX TABLESPACE hotelspace,
    plz       CHAR(5) NOT NULL CHECK (plz ~ '\d{5}'),
    ort       VARCHAR(40) NOT NULL
    )TABLESPACE hotelspace;
CREATE INDEX IF NOT EXISTS adresse_plz_idx ON adresse(plz) TABLESPACE hotelspace;

CREATE TABLE IF NOT EXISTS hotel (
    id            UUID PRIMARY KEY USING INDEX TABLESPACE hotelspace,
    -- http://www.h2database.com/html/datatypes.html#integer_type
    version       INTEGER NOT NULL DEFAULT 0,
    hotelname      VARCHAR(40) NOT NULL,
    adresse_id    UUID NOT NULL UNIQUE USING INDEX TABLESPACE hotelspace REFERENCES adresse(id),
    username      VARCHAR(20) NOT NULL,
    -- http://www.h2database.com/html/datatypes.html#timestamp_type
    erzeugt       TIMESTAMP NOT NULL,
    aktualisiert  TIMESTAMP NOT NULL
    )TABLESPACE hotelspace;

CREATE INDEX IF NOT EXISTS hotel_hotelname_idx ON hotel(hotelname) TABLESPACE hotelspace;

CREATE TABLE IF NOT EXISTS zimmer (
    id        UUID PRIMARY KEY USING INDEX TABLESPACE hotelspace,
    -- http://www.h2database.com/html/datatypes.html#numeric_type
    -- 10 Stellen, davon 2 Nachkommastellen
    zimmernummer INTEGER NOT NULL,
    istBelegt    BOOLEAN,
    anzahlBetten INTEGER,
    hotel_id  UUID REFERENCES hotel,
    idx       INTEGER NOT NULL DEFAULT 0
    )TABLESPACE hotelspace;
CREATE INDEX IF NOT EXISTS zimmer_hotel_id_idx ON zimmer(hotel_id) TABLESPACE hotelspace;
