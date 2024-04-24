package it.avbo.tps.hoolibo.trops.api.database;

import org.mariadb.jdbc.MariaDbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private static MariaDbDataSource ds;

    private static void createConnection() {
        try {
            ds = new MariaDbDataSource();
            ds.setUrl(System.getenv("DATABASE_HOST"));
            ds.setUser(System.getenv("DATABASE_USER"));
            ds.setPassword(System.getenv("DATABASE_PASSWORD"));

            loadTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadTables() {
        Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

        String createAccountTable =
                "CREATE TABLE IF NOT EXISTS `accounts` (\n" +
                "  `tipo` varchar(3) NOT NULL DEFAULT 'USR',\n" +
                "  `email` varchar(255) NOT NULL,\n" +
                "  `hash` varchar(255) NOT NULL,\n" +
                "  `nome` varchar(30) NOT NULL,\n" +
                "  `cognome` varchar(30) NOT NULL,\n" +
                "  `data_nascita` date NOT NULL,\n" +
                "  `cod_fis` varchar(16) NOT NULL,\n" +
                "  PRIMARY KEY (`email`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createEventTable =
                "CREATE TABLE `events` (\n" +
                "  `event_uuid` varchar(36) NOT NULL,\n" +
                "  `tipo` varchar(3) NOT NULL,\n" +
                "  `sport` varchar(9) NOT NULL,\n" +
                "  `data` timestamp NOT NULL,\n" +
                "  `max_partecipanti` int(11) NOT NULL,\n" +
                "  `titolo` varchar(100) NOT NULL,\n" +
                "  `descrizione` varchar(255) DEFAULT NULL,\n" +
                "  `organizer` varchar(255) NOT NULL,\n" +
                "  PRIMARY KEY (`event_uuid`),\n" +
                "  KEY `events_accounts_FK` (`organizer`),\n" +
                "  CONSTRAINT `events_accounts_FK` FOREIGN KEY (`organizer`) REFERENCES `accounts` (`email`) ON DELETE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        String createSubTable =
                "CREATE TABLE IF NOT EXISTS `subscriptions` (\n" +
                "  `user` varchar(255) NOT NULL,\n" +
                "  `event` varchar(36) NOT NULL,\n" +
                "  KEY `subscriptions_accounts_FK` (`user`),\n" +
                "  KEY `subscriptions_events_FK` (`event`),\n" +
                "  CONSTRAINT `subscriptions_accounts_FK` FOREIGN KEY (`user`) REFERENCES `accounts` (`email`) ON DELETE CASCADE,\n" +
                "  CONSTRAINT `subscriptions_events_FK` FOREIGN KEY (`event`) REFERENCES `events` (`event_uuid`) ON DELETE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        try (Connection conn = ds.getConnection()) {
            conn.prepareStatement(createAccountTable).execute();
            conn.prepareStatement(createEventTable).execute();
            conn.prepareStatement(createSubTable).execute();
        } catch (SQLException e) {
            logger.error("Errore nella creazione delle tabelle");
        }
    }

    public static Connection getConnection() {
        if (ds == null) {
            createConnection();
        }

        try {
            return ds.getConnection();
        } catch (SQLException e) {
            return null;
        }
    }
}
