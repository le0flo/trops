package it.avbo.tps.gruppotre.hoolibo.api.accounts.managers;

import java.util.HashMap;
import java.util.UUID;

public class SessionsManager {

    private static SessionsManager instance;

    private HashMap<UUID, String> activeSessions = new HashMap<>();

    public String retrieveAccount(UUID session) {
        return activeSessions.get(session);
    }

    public UUID generateSession(String email) {
        UUID uuid = UUID.randomUUID();

        if (activeSessions.get(uuid) != null) {
            return generateSession(email);
        } else {
            activeSessions.put(uuid, email);
            return uuid;
        }
    }

    public static SessionsManager getInstance() {
        if (instance == null) {
            instance = new SessionsManager();
        }

        return instance;
    }
}
