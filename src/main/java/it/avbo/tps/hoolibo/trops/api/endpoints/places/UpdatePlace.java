package it.avbo.tps.hoolibo.trops.api.endpoints.places;

import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/places/update")
public class UpdatePlace extends HttpServlet {

    // TODO da fare

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            if (account == null) {
                response.errorInvalidSessionUUID(resp);
            } else {
                response.success(resp, new JSONObject().put("TODO", "work in progress"));
            }
        } catch (IllegalArgumentException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
