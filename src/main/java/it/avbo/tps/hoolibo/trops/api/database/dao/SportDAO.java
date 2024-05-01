package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public JSONArray fetchAll() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        ResultSet rs = conn.prepareStatement(fetchAllSports).executeQuery();

        JSONArray result = new JSONArray();

        while (rs.next()) {
            JSONObject obj = new JSONObject();
            obj.put("uuid", rs.getString("sport_uuid"));
            obj.put("nome", rs.getString("sport_nome"));
            result.put(obj);
        }

        return result;
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
