package it.avbo.tps.gruppotre.hoolibo.api;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private static MariaDbDataSource ds;

    private static void createConnection() {
        try {
            ds = new MariaDbDataSource();
            ds.setUrl("jdbc:mariadb://localhost:3306/hoolibo");
            ds.setUser("root");
            ds.setPassword("athena");
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
