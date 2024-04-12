package it.avbo.tps.hoolibo.trops.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Utilities {

    public static Map<String, String> readPost(BufferedReader rd) throws IOException {
        Map<String, String> values = new HashMap<>();

        String line, key = null;
        while ((line = rd.readLine()) != null) {
            if (line.startsWith("Content-Disposition: form-data; name=\"")) {
                key = line.substring("Content-Disposition: form-data; name=\"".length(), line.length()-1);
            } else if (key != null && !line.isBlank()) {
                values.put(key, line);
                key = null;
            }
        }

        return values;
    }
}
