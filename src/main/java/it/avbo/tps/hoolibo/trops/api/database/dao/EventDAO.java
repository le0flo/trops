package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import it.avbo.tps.hoolibo.trops.api.utils.PasswordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.UUID;

public class EventDAO {

    private static EventDAO instance = null;

    private String fetchAllEvents, selectEvent, insertEvent, deleteEvent, insertSubscription, deleteSubscription;

    private EventDAO() {
        fetchAllEvents = "SELECT event_uuid, event_tipo, event_sport, event_place, event_data, event_max_partecipanti, event_titolo, event_descrizione, event_organizzatore FROM events";
        selectEvent = "SELECT event_tipo, event_sport, event_place, event_data, event_max_partecipanti, event_titolo, event_descrizione, event_organizzatore FROM events WHERE event_uuid = ?";
        insertEvent = "INSERT INTO events (event_uuid, event_tipo, event_sport, event_place, event_data, event_max_partecipanti, event_titolo, event_descrizione, event_organizzatore) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        deleteEvent = "DELETE FROM events WHERE event_uuid = ? AND event_organizzatore = ?";
        insertSubscription = "INSERT INTO subscriptions (user,event) VALUES (?,?)";
        deleteSubscription = "DELETE FROM subscriptions WHERE user = ? AND event = ?";
    }

    public JSONArray fetchAll() throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        ResultSet rs = conn.prepareStatement(fetchAllEvents).executeQuery();

        JSONArray result = new JSONArray();

        while (rs.next()) {
            JSONObject obj = new JSONObject();
            obj.put("uuid", rs.getString("event_uuid"));
            obj.put("tipo", rs.getString("event_tipo"));
            obj.put("sport", rs.getString("event_sport"));
            obj.put("place", rs.getString("event_place"));
            obj.put("data", rs.getString("event_data"));
            obj.put("max_partecipanti", rs.getString("event_max_partecipanti"));
            obj.put("titolo", rs.getString("event_titolo"));
            obj.put("description", rs.getString("event_descrizione"));
            result.put(obj);
        }

        return result;
    }

    public boolean insert(String tipo, String sport, String place, Timestamp data, int maxPartecipanti, String titolo, String descrizione, String organizer) throws SQLException {
        if (maxPartecipanti < 2) {
            return false;
        }

        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement insertEventStmt = conn.prepareStatement(insertEvent);

        UUID event_uuid = UUID.randomUUID();

        insertEventStmt.setString(1, event_uuid.toString());
        insertEventStmt.setString(2, tipo);
        insertEventStmt.setString(3, sport);
        insertEventStmt.setString(4, place);
        insertEventStmt.setTimestamp(5, data);
        insertEventStmt.setInt(6, maxPartecipanti);
        insertEventStmt.setString(7, titolo);
        insertEventStmt.setString(8, descrizione);
        insertEventStmt.setString(9, organizer);

        return insertEventStmt.executeUpdate() > 0;
    }

    public boolean delete(UUID uuid, String organizer) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement deleteEventStmt = conn.prepareStatement(deleteEvent);

        deleteEventStmt.setString(1, uuid.toString());
        deleteEventStmt.setString(2, organizer);

        return deleteEventStmt.executeUpdate() > 0;
    }

    public boolean subscribe(String user, UUID event) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement insertSubscriptionStmt = conn.prepareStatement(insertSubscription);

        insertSubscriptionStmt.setString(1, user);
        insertSubscriptionStmt.setString(2, event.toString());  // TODO controllare che non sia già iscritto all'evento

        return insertSubscriptionStmt.executeUpdate() > 0;
    }

    public boolean unsubscribe(String user, UUID event) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement deleteSubscriptionStmt = conn.prepareStatement(deleteSubscription);

        deleteSubscriptionStmt.setString(1, user);
        deleteSubscriptionStmt.setString(2, event.toString());

        return deleteSubscriptionStmt.executeUpdate() > 0;
    }

    public static EventDAO getInstance() {
        if (instance == null) {
            instance = new EventDAO();
        }

        return instance;
    }
}
