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
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
