package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlaceDAO {

    private static PlaceDAO instance = null;

    private String fetchAllPlaces, selectPlace, insertPlace;

    private PlaceDAO() {
        fetchAllPlaces = "SELECT place_uuid, place_cap, place_citta, place_indirizzo, place_civico, place_descrizione FROM places";
        selectPlace = "SELECT place_cap, place_citta, place_indirizzo, place_civico, place_descrizione FROM places WHERE place_uuid = ?";
        insertPlace = "INSERT INTO places (place_uuid, place_cap, place_citta, place_indirizzo, place_civico, place_descrizione) VALUES (?, ?, ?, ?, ?, ?)";
    }

    public JSONArray fetchAll() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        ResultSet rs = conn.prepareStatement(fetchAllPlaces).executeQuery();

        JSONArray result = new JSONArray();

        while (rs.next()) {
            JSONObject obj = new JSONObject();
            obj.put("uuid", rs.getString("place_uuid"));
            obj.put("cap", rs.getString("place_cap"));
            obj.put("citta", rs.getString("place_citta"));
            obj.put("indirizzo", rs.getString("place_indirizzo"));
            obj.put("civico", rs.getString("place_civico"));
            obj.put("descrizione", rs.getString("place_descrizione"));
            result.put(obj);
        }

        return result;
    }

    public boolean insert(String cap, String citta, String indirizzo, String civico, String descrizione) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement insertPlaceStmt = conn.prepareStatement(insertPlace);

        UUID place_uuid = UUID.randomUUID();

        insertPlaceStmt.setString(1, place_uuid.toString());
        insertPlaceStmt.setString(2, cap);
        insertPlaceStmt.setString(3, citta);
        insertPlaceStmt.setString(4, indirizzo);
        insertPlaceStmt.setString(5, civico);
        insertPlaceStmt.setString(6, descrizione);

        return insertPlaceStmt.executeUpdate() > 0;
    }

    public static PlaceDAO getInstance() {
        if (instance == null) {
            instance = new PlaceDAO();
        }

        return instance;
    }
}
