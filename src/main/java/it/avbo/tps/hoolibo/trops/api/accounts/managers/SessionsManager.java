package it.avbo.tps.hoolibo.trops.api.accounts.managers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.UUID;

public class SessionsManager {

    private static SessionsManager instance;

    private HashMap<UUID, String> activeSessions = new HashMap<>();

    private JedisPool pool;

    public SessionsManager() {
        pool = new JedisPool(System.getenv("REDIS_HOST"), 6379);
    }

    public UUID generateSession(String email) {
        UUID uuid = UUID.randomUUID();

        if (activeSessions.get(uuid) != null) {
            return generateSession(email);
        } else {
            try (Jedis jedis = pool.getResource()) {
                jedis.set(uuid.toString(), email);
            }
            return uuid;
        }
    }

    public String retrieveAccount(UUID session) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(session.toString());
        }
    }

    public UUID unregisterSession(UUID session) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(session.toString());
        }
        return session;
    }

    public static SessionsManager getInstance() {
        if (instance == null) {
            instance = new SessionsManager();
        }

        return instance;
    }
}
