package it.avbo.tps.hoolibo.trops.api.accounts.endpoints;

import it.avbo.tps.hoolibo.trops.api.ConnectionFactory;
import it.avbo.tps.hoolibo.trops.api.Utilities;
import it.avbo.tps.hoolibo.trops.api.accounts.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.accounts.managers.SessionsManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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

    private String selectUser = "SELECT hash, tipo FROM accounts WHERE email = ?";

    private byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];

        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return binary;
    }

    private boolean check(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;

        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }

        return diff == 0;
    }

    public boolean validatePassword(String password, String goodHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] params = goodHash.split(":");

        int iterations = Integer.parseInt(params[0]);
        byte[] salt = fromHex(params[1]);
        byte[] hash = fromHex(params[2]);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, RegisterUser.HASH_BYTES * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] test = skf.generateSecret(spec).getEncoded();

        return check(hash, test);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        SessionsManager sessionsManager = SessionsManager.getInstance();
        PrintWriter writer = resp.getWriter();

        Map<String, String> postData = Utilities.readPost(req.getReader());

        String session = postData.get("session");
        String email = postData.get("email");
        String password = postData.get("password");

        try (Connection connection = ConnectionFactory.getConnection(); PreparedStatement selectUserStatement = connection.prepareStatement(selectUser)) {
            if (session != null) {
                email = sessionsManager.retrieveAccount(UUID.fromString(session));

                if (email != null) {
                    selectUserStatement.setString(1, email);
                    ResultSet rs = selectUserStatement.executeQuery();
                    JSONObject values = new JSONObject();

                    if (rs.next()) {
                        values.put("tipo", rs.getString("tipo"));
                    }

                    writer.println(response.success(values));
                } else {
                    writer.println(response.errorInvalidSessionUUID());
                    resp.setStatus(500);
                }
            } else if (email == null || password == null) {
                writer.println(response.errorNullFields());
                resp.setStatus(500);
            } else {
                selectUserStatement.setString(1, email);
                ResultSet rs = selectUserStatement.executeQuery();

                if (rs.next()) {
                    String hash = rs.getString("hash");

                    if (validatePassword(password, hash)) {
                        UUID sessionUUID = sessionsManager.generateSession(email);

                        JSONObject values = new JSONObject();
                        values.put("uuid-sessione", sessionUUID);
                        values.put("tipo", rs.getString("tipo"));

                        writer.println(response.success(values));
                    } else {
                        writer.println(response.errorInvalidLoginCredentials());
                        resp.setStatus(500);
                    }
                } else {
                    writer.println(response.errorNotRegistered());
                    resp.setStatus(500);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.handleException(e, this.getClass());
            resp.setStatus(500);
        }
    }
}
