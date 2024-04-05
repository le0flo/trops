package it.avbo.tps.gruppotre.hoolibo.api.accounts.managers;

import org.json.JSONObject;

public class ResponseManager {

    private static ResponseManager instance;

    private JSONObject response = new JSONObject();

    public String success(JSONObject values) {
        response.clear();
        response.put("esito", "successo");
        response.put("codice", 0);
        response.put("risultato", values);
        return response.toString();
    }

    public String errorNullFields() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 1);
        response.put("motivazione", "uno o più campi risultano nulli");
        return response.toString();
    }

    public String errorAlreadyRegistered() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 2);
        response.put("motivazione", "esiste già un utente con quel nome");
        return response.toString();
    }

    public String errorInvalidSessionUUID() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 3);
        response.put("motivazione", "L'uuid di sessione non è valido");
        return response.toString();
    }

    public String errorInvalidLoginCredentials() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 4);
        response.put("motivazione", "Le credenziali di login sono errate");
        return response.toString();
    }

    public String errorNotRegistered() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 5);
        response.put("motivazione", "L'utente non è registrato");
        return response.toString();
    }

    public static ResponseManager getInstance() {
        if (instance == null) {
            instance = new ResponseManager();
        }

        return instance;
    }
}
