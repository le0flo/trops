package it.avbo.tps.hoolibo.trops.api.endpoints.accounts;

import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        SessionsManager sessionsManager = SessionsManager.getInstance();
        Map<String, String> requestBodyMap = GeneralUtils.readPost(req.getReader());

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            String deactivatedSession = requestBodyMap.get("deactivated-session");
            String deactivatedSessionAccount = GeneralUtils.checkSession(deactivatedSession);

            if (account == null || deactivatedSessionAccount == null) {
                response.errorInvalidSessionUUID(resp);
            } else if (account.equals(deactivatedSessionAccount)) {
                UUID unregisteredSession = UUID.fromString(deactivatedSession);
                sessionsManager.unregisterSession(unregisteredSession);

                JSONObject values = new JSONObject();
                values.put("sessione-disattivata", unregisteredSession.toString());

                response.success(resp, values);
            } else {
                response.errorSessionNotOwned(resp);
            }
        } catch (IllegalArgumentException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
