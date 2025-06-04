# Crea il database

CREATE DATABASE IF NOT EXISTS trops;

# Crea le varie tabelle

--
-- Table structure for table `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
    `user_email` varchar(255) NOT NULL,
    `user_tipo` varchar(3) NOT NULL DEFAULT 'USR',
    `user_hash` varchar(255) NOT NULL,
    `user_nome` varchar(30) NOT NULL,
    `user_cognome` varchar(30) NOT NULL,
    `user_data_nascita` date NOT NULL,
    `user_cod_fis` varchar(16) NOT NULL,
    PRIMARY KEY (`user_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `places`
--

CREATE TABLE IF NOT EXISTS `places` (
    `place_uuid` varchar(36) NOT NULL,
    `place_cap` varchar(5) NOT NULL,
    `place_citta` varchar(100) NOT NULL,
    `place_indirizzo` varchar(128) NOT NULL,
    `place_civico` varchar(5) NOT NULL,
    `place_descrizione` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`place_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `sports`
--

CREATE TABLE IF NOT EXISTS `sports` (
    `sport_uuid` varchar(36) NOT NULL,
    `sport_nome` varchar(60) NOT NULL,
    PRIMARY KEY (`sport_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `events`
--

CREATE TABLE IF NOT EXISTS `events` (
    `event_uuid` varchar(36) NOT NULL,
    `event_tipo` varchar(3) NOT NULL,
    `event_sport` varchar(36) NOT NULL,
    `event_place` varchar(36) NOT NULL,
    `event_data` timestamp NOT NULL,
    `event_max_partecipanti` int(11) NOT NULL,
    `event_titolo` varchar(100) NOT NULL,
    `event_descrizione` varchar(255) DEFAULT NULL,
    `event_organizzatore` varchar(255) NOT NULL,
    PRIMARY KEY (`event_uuid`),
    KEY `events_accounts_FK` (`event_organizzatore`),
    KEY `events_sports_FK` (`event_sport`),
    KEY `events_places_FK` (`event_place`),
    CONSTRAINT `events_accounts_FK` FOREIGN KEY (`event_organizzatore`) REFERENCES `accounts` (`user_email`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `events_places_FK` FOREIGN KEY (`event_place`) REFERENCES `places` (`place_uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `events_sports_FK` FOREIGN KEY (`event_sport`) REFERENCES `sports` (`sport_uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Table structure for table `subscriptions`
--

CREATE TABLE IF NOT EXISTS `subscriptions` (
    `user` varchar(255) NOT NULL,
    `event` varchar(36) NOT NULL,
    KEY `subscriptions_accounts_FK` (`user`),
    KEY `subscriptions_events_FK` (`event`),
    CONSTRAINT `subscriptions_accounts_FK` FOREIGN KEY (`user`) REFERENCES `accounts` (`user_email`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `subscriptions_events_FK` FOREIGN KEY (`event`) REFERENCES `events` (`event_uuid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;