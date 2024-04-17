package it.avbo.tps.hoolibo.trops.api.endpoints.events;

import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;
import it.avbo.tps.hoolibo.trops.api.utils.Utilities;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/events/delete")
public class DeleteEvent extends HttpServlet {

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

            if (account == null) {
                writer.println(response.errorInvalidSessionUUID());
                resp.setStatus(500);
            } else {
                writer.println(response.success(null));
            }
        } catch (IllegalArgumentException e) {
            response.handleException(e, this.getClass());
            resp.setStatus(500);
        }
    }
}
