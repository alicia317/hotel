-- Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.

-- http://www.h2database.com/html/commands.html#insert
INSERT INTO login (id, username, password, rollen)
VALUES
    ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g','ADMIN,USER,ACTUATOR'),
    ('30000000-0000-0000-0000-000000000001','user','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g', 'USER');

INSERT INTO adresse (id, plz, ort)
VALUES
    ('20000000-0000-0000-0000-000000000000','00000','Achern'),
    ('20000000-0000-0000-0000-000000000001','11111','Berlin'),
    ('20000000-0000-0000-0000-000000000020','22222','Baden-Baden'),
    ('20000000-0000-0000-0000-000000000030','33333','Köln'),
    ('20000000-0000-0000-0000-000000000040','44444','Dortmund'),
    ('20000000-0000-0000-0000-000000000050','55555','Essen'),
    ('20000000-0000-0000-0000-000000000060','66666','Freiburg');

INSERT INTO hotel (id, version, hotelname, adresse_id, username, erzeugt, aktualisiert)
VALUES
    -- admin
    ('00000000-0000-0000-0000-000000000000',0,'Admin','20000000-0000-0000-0000-000000000000','admin','2024-01-31 00:00:00','2024-01-31 00:00:00'),
    -- HTTP GET
    ('00000000-0000-0000-0000-000000000001',0,'Alpha','20000000-0000-0000-0000-000000000001','user','2024-01-01 00:00:00','2024-01-01 00:00:00'),
    ('00000000-0000-0000-0000-000000000020',0,'Alpha','20000000-0000-0000-0000-000000000020','user','2022-01-02 00:00:00','2022-01-02 00:00:00'),
    -- HTTP PUT
    ('00000000-0000-0000-0000-000000000030',0,'Alpha','20000000-0000-0000-0000-000000000030','user','2022-01-03 00:00:00','2022-01-03 00:00:00'),
    -- HTTP PATCH
    ('00000000-0000-0000-0000-000000000040',0,'Delta','20000000-0000-0000-0000-000000000040','user','2022-01-04 00:00:00','2022-01-04 00:00:00'),
    -- HTTP DELETE
    ('00000000-0000-0000-0000-000000000050',0,'Epsilon','20000000-0000-0000-0000-000000000050','user','2022-01-05 00:00:00','2022-01-05 00:00:00'),
    -- zur freien Verfuegung
    ('00000000-0000-0000-0000-000000000060',0,'Phi','20000000-0000-0000-0000-000000000060','user','2022-01-06 00:00:00','2022-01-06 00:00:00');

INSERT INTO zimmer (id, zimmernummer, istBelegt, anzahlBetten, hotel_id, idx)
VALUES
    ('10000000-0000-0000-0000-000000000000',11,true,4,'00000000-0000-0000-0000-000000000000',0),
    ('10000000-0000-0000-0000-000000000010',22,true,2,'00000000-0000-0000-0000-000000000001',0),
    ('10000000-0000-0000-0000-000000000011',13,false,1,'00000000-0000-0000-0000-000000000001',1),
    ('10000000-0000-0000-0000-000000000012',2,false,4,'00000000-0000-0000-0000-000000000001',2),
    ('10000000-0000-0000-0000-000000000020',4,false,2,'00000000-0000-0000-0000-000000000020',0),
    ('10000000-0000-0000-0000-000000000030',5,true,2,'00000000-0000-0000-0000-000000000030',1),
    ('10000000-0000-0000-0000-000000000031',20,true,3,'00000000-0000-0000-0000-000000000030',0),
    ('10000000-0000-0000-0000-000000000040',10,false,4,'00000000-0000-0000-0000-000000000040',0);
