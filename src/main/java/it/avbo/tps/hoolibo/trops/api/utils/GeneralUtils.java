package it.avbo.tps.hoolibo.trops.api.utils;

import it.avbo.tps.hoolibo.trops.api.endpoints.accounts.RegisterUser;
import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeneralUtils {

    public static Map<String, String> readPost(BufferedReader rd) throws IOException {
        Map<String, String> values = new HashMap<>();

        String line, key = null;
        while ((line = rd.readLine()) != null) {
            if (line.startsWith("Content-Disposition: form-data; name=\"")) {
                key = line.substring("Content-Disposition: form-data; name=\"".length(), line.length()-1);
            } else if (key != null && !line.isBlank()) {
                if (!line.startsWith("-----------------------------")) {
                    values.put(key, line);
                }
                key = null;
            }
        }

        return values;
    }

    public static String checkSession(String authToken) {
        SessionsManager sessionsManager = SessionsManager.getInstance();

        if (authToken == null) {
            return null;
        } else {
            try {
                UUID authUUID = UUID.fromString(authToken);
                return sessionsManager.retrieveAccount(authUUID);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
