package it.avbo.tps.hoolibo.trops.api.endpoints.places;

import it.avbo.tps.hoolibo.trops.api.database.dao.PlaceDAO;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/places/create")
public class CreatePlace extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        Map<String, String> requestBodyMap = GeneralUtils.readPost(req.getReader());

        String cap = requestBodyMap.get("cap");
        String citta = requestBodyMap.get("citta");
        String indirizzo = requestBodyMap.get("indirizzo");
        String civico = requestBodyMap.get("civico");
        String descrizione = requestBodyMap.get("descrizione");

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            // TODO controllo per il tipo di account

            if (account == null) {
                response.errorInvalidSessionUUID(resp);
            } else if (cap == null || citta == null || indirizzo == null || civico == null) {
                response.errorNullFields(resp);
            } else if (cap.length() == 5 && GeneralUtils.isNumbersOnly(cap)) {
                boolean success = PlaceDAO.getInstance().insert(cap, citta, indirizzo, civico, descrizione);

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
