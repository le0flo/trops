package it.avbo.tps.gruppotre.hoolibo.api;

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
                "  `cod_scuola` varchar(10) NOT NULL,\n" +
                "  PRIMARY KEY (`email`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

        try (Connection conn = ds.getConnection()) {
            conn.prepareStatement(createAccountTable).execute();
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
