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

-- (1) PowerShell:
--     cd extras\compose\mysql
--     docker compose up
-- (1) 2. PowerShell:
--     cd extras\compose\mysql
--     docker compose exec db bash
--         mysql --user=root --password=p < /sql/create-db-kunde.sql
--         exit
--     docker compose down

-- mysqlsh ist *NICHT* im Docker-Image enthalten: https://dev.mysql.com/doc/refman/8.2/en/mysql.html

-- https://dev.mysql.com/doc/refman/8.2/en/create-user.html
-- https://dev.mysql.com/doc/refman/8.2/en/role-names.html
CREATE USER IF NOT EXISTS hotel IDENTIFIED BY 'p';
GRANT USAGE ON *.* TO hotel;

-- https://dev.mysql.com/doc/refman/8.2/en/create-database.html
-- https://dev.mysql.com/doc/refman/8.2/en/charset.html
-- SHOW CHARACTER SET;
CREATE DATABASE IF NOT EXISTS hotel CHARACTER SET utf8;

GRANT ALL ON hotel.* to hotel;

-- https://dev.mysql.com/doc/refman/8.2/en/create-tablespace.html
-- .idb-Datei innerhalb vom "data"-Verzeichnis
CREATE TABLESPACE `hotelspace` ADD DATAFILE 'hotelspace.ibd' ENGINE=INNODB;
