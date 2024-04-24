# Crea il database

CREATE DATABASE IF NOT EXISTS trops;

# Crea le varie tabelle

CREATE TABLE IF NOT EXISTS `accounts` (
    `tipo` varchar(3) NOT NULL DEFAULT 'USR',
    `email` varchar(255) NOT NULL,
    `hash` varchar(255) NOT NULL,
    `nome` varchar(30) NOT NULL,
    `cognome` varchar(30) NOT NULL,
    `data_nascita` date NOT NULL,
    `cod_fis` varchar(16) NOT NULL,
    PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `events` (
    `event_uuid` varchar(36) NOT NULL,
    `tipo` varchar(3) NOT NULL,
    `sport` varchar(9) NOT NULL,
    `data` timestamp NOT NULL,
    `max_partecipanti` int(11) NOT NULL,
    `titolo` varchar(100) NOT NULL,
    `descrizione` varchar(255) DEFAULT NULL,
    `organizer` varchar(255) NOT NULL,
    PRIMARY KEY (`event_uuid`),
    KEY `events_accounts_FK` (`organizer`),
    CONSTRAINT `events_accounts_FK` FOREIGN KEY (`organizer`) REFERENCES `accounts` (`email`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `subscriptions` (
    `user` varchar(255) NOT NULL,
    `event` varchar(36) NOT NULL,
    KEY `subscriptions_accounts_FK` (`user`),
    KEY `subscriptions_events_FK` (`event`),
    CONSTRAINT `subscriptions_accounts_FK` FOREIGN KEY (`user`) REFERENCES `accounts` (`email`) ON DELETE CASCADE,
    CONSTRAINT `subscriptions_events_FK` FOREIGN KEY (`event`) REFERENCES `events` (`event_uuid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;