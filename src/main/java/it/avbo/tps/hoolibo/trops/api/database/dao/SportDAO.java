package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SportDAO {

    private static SportDAO instance = null;

    private String fetchAllSports, selectSport, insertSport;

    private SportDAO() {
        fetchAllSports = "SELECT sport_uuid, sport_nome FROM sports";
        selectSport = "SELECT sport_nome FROM sports WHERE sport_uuid = ?";
        insertSport = "INSERT INTO sports (sport_uuid, sport_nome) VALUES (?, ?)";
    }

    public boolean insert(String nome) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement insertSportStmt = conn.prepareStatement(insertSport);

        UUID sport_uuid = UUID.randomUUID();

        insertSportStmt.setString(1, sport_uuid.toString());
        insertSportStmt.setString(2, nome);

        return insertSportStmt.executeUpdate() > 0;
    }

    public static SportDAO getInstance() {
        if (instance == null) {
            instance = new SportDAO();
        }

        return instance;
    }
}
