package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public boolean insert(String cap, String citta, String indirizzo, String civico) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement insertPlaceStmt = conn.prepareStatement(insertPlace);

        UUID place_uuid = UUID.randomUUID();

        insertPlaceStmt.setString(1, place_uuid.toString());
        insertPlaceStmt.setString(2, cap);
        insertPlaceStmt.setString(3, citta);
        insertPlaceStmt.setString(4, indirizzo);
        insertPlaceStmt.setString(5, civico);

        return insertPlaceStmt.executeUpdate() > 0;
    }

    public static PlaceDAO getInstance() {
        if (instance == null) {
            instance = new PlaceDAO();
        }

        return instance;
    }
}
