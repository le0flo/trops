package it.avbo.tps.hoolibo.trops.api.endpoints.sports;

import it.avbo.tps.hoolibo.trops.api.database.dao.SportDAO;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/sports/create")
public class CreateSport extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        Map<String, String> requestBodyMap = GeneralUtils.readPost(req.getReader());

        String nome = requestBodyMap.get("nome");

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            // TODO controllo per il tipo di account

            if (account == null) {
                response.errorInvalidSessionUUID(resp);
            } else if (nome == null) {
                response.errorNullFields(resp);
            } else {
                boolean success = SportDAO.getInstance().insert(nome);

                if (success) {
                    response.success(resp, null);
                } else {
                    response.errorInvalidFieldFormat(resp);
                }
            }
        } catch (IllegalArgumentException | SQLException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
