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
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/events/unsubscribe")
public class UnsubcribeEvent extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        Map<String, String> requestBodyMap = GeneralUtils.readPost(req.getReader());

        String uuid = requestBodyMap.get("uuid");

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            if (account == null) {
                response.errorInvalidSessionUUID(resp);
            } else if (uuid == null) {
                response.errorNullFields(resp);
            } else {
                boolean success = EventDAO.getInstance().unsubscribe(account, UUID.fromString(uuid));

                if (success) {
                    response.success(resp, null);
                } else {
                    response.errorNotSubscribed(resp);
                }
            }
        } catch (IllegalArgumentException | SQLException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
