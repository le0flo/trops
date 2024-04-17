package it.avbo.tps.hoolibo.trops.api.endpoints.accounts;

import it.avbo.tps.hoolibo.trops.api.utils.Utilities;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

@WebServlet("/account/session/deactivate")
public class DeactivateSession extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        SessionsManager sessionsManager = SessionsManager.getInstance();
        ResponseManager response = ResponseManager.getInstance();
        PrintWriter writer = resp.getWriter();

        Map<String, String> postData = Utilities.readPost(req.getReader());

        try {
            String authorization = req.getHeader("Authorization");
            String account = Utilities.checkSession(authorization);

            String deactivatedSession = postData.get("deactivated-session");
            String deactivatedSessionAccount = Utilities.checkSession(deactivatedSession);

            if (account == null || deactivatedSessionAccount == null) {
                writer.println(response.errorInvalidSessionUUID());
                resp.setStatus(500);
            } else if (account.equals(deactivatedSessionAccount)) {
                UUID unregisteredSession = UUID.fromString(deactivatedSession);
                sessionsManager.unregisterSession(unregisteredSession);

                JSONObject values = new JSONObject();
                values.put("sessione-disattivata", unregisteredSession.toString());

                writer.println(response.success(values));
            } else {
                writer.println(response.errorSessionNotOwned());
                resp.setStatus(500);
            }
        } catch (IllegalArgumentException e) {
            response.handleException(e, this.getClass());
            resp.setStatus(500);
        }
    }
}
