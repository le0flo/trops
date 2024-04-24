package it.avbo.tps.hoolibo.trops.api.utils;

import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class GeneralUtils {

    public static Map<String, String> readPost(BufferedReader rd) throws IOException {
        Map<String, String> values = new HashMap<>();
        List<String> lines = new ArrayList<>();

        String line, key = null;
        while ((line = rd.readLine()) != null) {
            lines.add(line);
            if (line.startsWith("Content-Disposition: form-data; name=\"")) {
                key = line.substring("Content-Disposition: form-data; name=\"".length(), line.length()-1);
            } else if (key != null && !line.isBlank()) {
                if (!line.startsWith("-----------------------------")) {
                    values.put(key, line);
                }
                key = null;
            }
        }

        if (lines.size() == 1) {
            String[] inlineValues = lines.get(0).split("&");

            for (String inlineValue : inlineValues) {
                String[] splittedValue = inlineValue.split("=");

                key = splittedValue[0];
                String value = splittedValue[1];
                int indexOfPercentage = -1;
                while ((indexOfPercentage = value.indexOf("%")) != -1) {
                    String oldChar = line.substring(indexOfPercentage, indexOfPercentage+2);
                    value.replace(oldChar, convertCode(oldChar));
                }

                values.put(key, value);
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

    private static String convertCode(String code) {
        switch (code) {
            case "%20": {
                return " ";
            }
            case "%3C": {
                return "<";
            }
            case "%3E": {
                return ">";
            }
            case "%23": {
                return "#";
            }
            case "%25": {
                return "%";
            }
            case "%2B": {
                return "+";
            }
            case "%7B": {
                return "{";
            }
            case "%7D": {
                return "}";
            }
            case "%7C": {
                return "|";
            }
            case "%5C": {
                return "\\";
            }
            case "%5E": {
                return "^";
            }
            case "%7E": {
                return "~";
            }
            case "%5B": {
                return "[";
            }
            case "%5D": {
                return "]";
            }
            case "%60": {
                return "‘";
            }
            case "%3B": {
                return ";";
            }
            case "%2F": {
                return "/";
            }
            case "%3F": {
                return "?";
            }
            case "%3A": {
                return ":";
            }
            case "%40": {
                return "@";
            }
            case "%3D": {
                return "=";
            }
            case "%26": {
                return "&";
            }
            case "%24": {
                return "$";
            }
            default: {
                return null;
            }
        }
    }
}
