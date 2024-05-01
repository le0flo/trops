package it.avbo.tps.hoolibo.trops.api.endpoints.events;

import it.avbo.tps.hoolibo.trops.api.database.dao.EventDAO;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/events/create")
public class CreateEvent extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        Map<String, String> requestBodyMap = GeneralUtils.readPost(req.getReader());

        String tipo = requestBodyMap.get("tipo");
        String sport = requestBodyMap.get("sport_uuid");
        String place = requestBodyMap.get("sport_uuid");
        String data_event = requestBodyMap.get("data");
        String max_partecipanti = requestBodyMap.get("max_partecipanti");
        String titolo = requestBodyMap.get("titolo");
        String descrizione = requestBodyMap.get("descrizione");

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            // TODO controllo per il tipo di account

            if (account == null) {
                response.errorInvalidSessionUUID(resp);
            } else if (tipo == null || sport == null || place == null || data_event == null || max_partecipanti == null || titolo == null) {
                response.errorNullFields(resp);
            } else if (tipo.equals("SCO") || tipo.equals("EXT")) {
                int maxPartecipanti = Integer.parseInt(max_partecipanti);

                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d hh:mm:ss");
                Timestamp data = Timestamp.valueOf(LocalDateTime.parse(data_event ,df));

                boolean success = EventDAO.getInstance().insert(tipo, sport, place, data, maxPartecipanti, titolo, descrizione, account);

                if (success) {
                    response.success(resp, null);
                } else {
                    response.errorInvalidFieldFormat(resp);
                }
            } else {
                response.errorInvalidFieldFormat(resp);
            }
        } catch (IllegalArgumentException | SQLException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
