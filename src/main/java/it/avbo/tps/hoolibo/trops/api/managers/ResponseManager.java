package it.avbo.tps.hoolibo.trops.api.managers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

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

    public void handleException(Exception e, Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("#### ERRORE ####");
        errorBuilder.append("Orario dell'errore: " + LocalDateTime.now());
        errorBuilder.append("Posizione:");
        errorBuilder.append("\t" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            errorBuilder.append("\t\t" + element.getClassName() + ": " + element.getLineNumber());
        }
        errorBuilder.append("################");

        logger.error(errorBuilder.toString());
    }

    public String errorNullFields() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 1);
        response.put("motivazione", "Uno o più campi risultano nulli");
        return response.toString();
    }

    public String errorInvalidFieldFormat() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 2);
        response.put("motivazione", "Uno o più campi non rispettano i formati prestabiliti");
        return response.toString();
    }

    public String errorAlreadyRegistered() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 3);
        response.put("motivazione", "Esiste già un utente con quel nome");
        return response.toString();
    }

    public String errorInvalidSessionUUID() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 4);
        response.put("motivazione", "L'uuid di sessione non è valido");
        return response.toString();
    }

    public String errorInvalidLoginCredentials() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 5);
        response.put("motivazione", "Le credenziali di login sono errate");
        return response.toString();
    }

    public String errorNotRegistered() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 6);
        response.put("motivazione", "L'utente non è registrato");
        return response.toString();
    }

    public String errorSessionNotOwned() {
        response.clear();
        response.put("esito", "fallito");
        response.put("codice", 7);
        response.put("motivazione", "Non sei il proprietario della sessione indicata");
        return response.toString();
    }

    public static ResponseManager getInstance() {
        if (instance == null) {
            instance = new ResponseManager();
        }

        return instance;
    }
}
