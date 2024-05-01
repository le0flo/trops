package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import it.avbo.tps.hoolibo.trops.api.utils.PasswordUtils;

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

    public boolean insert(String tipo, String sport, Timestamp data, int maxPartecipanti, String titolo, String descrizione, String organizer) throws SQLException {
        if (maxPartecipanti < 2) {
            return false;
        }

        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement insertEventStmt = conn.prepareStatement(insertEvent);

        UUID event_uuid = UUID.randomUUID();

        insertEventStmt.setString(1, event_uuid.toString());
        insertEventStmt.setString(2, tipo);
        insertEventStmt.setString(3, sport.toLowerCase());
        insertEventStmt.setTimestamp(4, data);
        insertEventStmt.setInt(5, maxPartecipanti);
        insertEventStmt.setString(6, titolo);
        insertEventStmt.setString(7, descrizione);
        insertEventStmt.setString(8, organizer);

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
