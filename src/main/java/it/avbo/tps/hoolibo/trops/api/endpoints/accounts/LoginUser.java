package it.avbo.tps.hoolibo.trops.api.endpoints.accounts;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import it.avbo.tps.hoolibo.trops.api.database.dao.UserDAO;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;
import it.avbo.tps.hoolibo.trops.api.utils.PasswordUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/account/login")
public class LoginUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        SessionsManager sessionsManager = SessionsManager.getInstance();
        Map<String, String> postData = GeneralUtils.readPost(req.getReader());

        String session = postData.get("session");
        String email = postData.get("email");
        String password = postData.get("password");

        try {
            if (session == null && (email == null || password == null)) {
                response.errorNullFields(resp);
            } else if (session == null) {
                String hash = UserDAO.getInstance().selectHash(email);

                if (hash == null) {
                    response.errorNotRegistered(resp);
                } else if (PasswordUtils.validate(password, hash)) {
                    UUID sessionUUID = sessionsManager.generateSession(email);

                    JSONObject values = new JSONObject();
                    values.put("uuid-sessione", sessionUUID);

                    response.success(resp, values);
                } else {
                    response.errorInvalidLoginCredentials(resp);
                }
            } else {
                email = sessionsManager.retrieveAccount(UUID.fromString(session));

                if (email == null) {
                    response.errorInvalidSessionUUID(resp);
                } else {
                    response.success(resp, null);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
