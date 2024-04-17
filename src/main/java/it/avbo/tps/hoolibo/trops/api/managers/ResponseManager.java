package it.avbo.tps.hoolibo.trops.api.managers;

import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class ResponseManager {

    private static ResponseManager instance;

    private JSONObject message = new JSONObject();

    public void success(HttpServletResponse resp, JSONObject values) throws IOException {
        message.clear();
        message.put("esito", "successo");
        message.put("codice", 0);
        message.put("risultato", values);

        resp.getWriter().println(message.toString());
        resp.setStatus(200);
    }

    public void handleException(HttpServletResponse resp, Exception e, Class<?> clazz) throws IOException {
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

        message.clear();
        message.put("esito", "fallito");
        message.put("codice", -1);
        message.put("motivazione", "Errore interno");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorNullFields(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 1);
        message.put("motivazione", "Uno o più campi risultano nulli");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorInvalidFieldFormat(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 2);
        message.put("motivazione", "Uno o più campi non rispettano i formati prestabiliti");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorAlreadyRegistered(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 3);
        message.put("motivazione", "Esiste già un utente con quel nome");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorInvalidSessionUUID(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 4);
        message.put("motivazione", "L'uuid di sessione non è valido");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorInvalidLoginCredentials(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 5);
        message.put("motivazione", "Le credenziali di login sono errate");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorNotRegistered(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 6);
        message.put("motivazione", "L'utente non è registrato");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public void errorSessionNotOwned(HttpServletResponse resp) throws IOException {
        message.clear();
        message.put("esito", "fallito");
        message.put("codice", 7);
        message.put("motivazione", "Non sei il proprietario della sessione indicata");

        resp.getWriter().println(message.toString());
        resp.setStatus(500);
    }

    public static ResponseManager getInstance() {
        if (instance == null) {
            instance = new ResponseManager();
        }

        return instance;
    }
}
