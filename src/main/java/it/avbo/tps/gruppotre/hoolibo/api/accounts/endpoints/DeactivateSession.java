package it.avbo.tps.gruppotre.hoolibo.api.accounts.endpoints;

import it.avbo.tps.gruppotre.hoolibo.api.accounts.managers.ResponseManager;
import it.avbo.tps.gruppotre.hoolibo.api.accounts.managers.SessionsManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/account/session/deactivate")
public class DeactivateSession extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        SessionsManager sessionsManager = SessionsManager.getInstance();
        ResponseManager response = ResponseManager.getInstance();
        PrintWriter writer = resp.getWriter();

        try {
            UUID authorization = UUID.fromString(req.getHeader("Authorization"));
            UUID deactivatedSession = UUID.fromString(req.getParameter("deactivated-session"));

            String account = sessionsManager.retrieveAccount(authorization);
            String deactivatedSessionAccount = sessionsManager.retrieveAccount(deactivatedSession);

            if (account == null || deactivatedSessionAccount == null) {
                writer.println(response.errorInvalidSessionUUID());
                resp.sendError(500);
            } else if (account.equals(deactivatedSessionAccount)) {
                UUID unregisteredSession = sessionsManager.unregisterSession(deactivatedSession);

                JSONObject values = new JSONObject();
                values.put("sessione-disattivata", unregisteredSession.toString());

                writer.println(response.success(values));
            } else {
                writer.println(response.errorSessionNotOwned());
                resp.sendError(500);
            }
        } catch (IllegalArgumentException e) {
            response.handleException(e, this.getClass());
            resp.sendError(500);
        }
    }
}
