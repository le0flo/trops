package it.avbo.tps.gruppotre.hoolibo.api.endpoints;

import it.avbo.tps.gruppotre.hoolibo.api.ConnectionFactory;
import it.avbo.tps.gruppotre.hoolibo.api.managers.SessionsManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/login")
public class Login extends HttpServlet {

    private String selectUser = "SELECT hash FROM accounts WHERE email = ?";

    private byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];

        for(int i = 0; i < binary.length; i++) {
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }

        return binary;
    }

    private boolean check(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;

        for(int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }

        return diff == 0;
    }

    public boolean validatePassword(String password, String goodHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] params = goodHash.split(":");

        int iterations = Integer.parseInt(params[0]);
        byte[] salt = fromHex(params[1]);
        byte[] hash = fromHex(params[2]);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, Register.HASH_BYTES * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] test = skf.generateSecret(spec).getEncoded();

        return check(hash, test);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionsManager sessionsManager = SessionsManager.getInstance();

        String session = req.getParameter("session");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (session != null) {
            if (sessionsManager.retrieveAccount(UUID.fromString(session)) != null) {
                resp.getWriter().println("valid"); // TODO standardizzare le risposte
            } else {
                resp.sendError(511); // TODO standardizzare le risposte
            }
        } else if (email == null || password == null) {
            resp.sendError(501); // TODO standardizzare le risposte
        } else {
            try (Connection connection = ConnectionFactory.getConnection()) {
                PreparedStatement selectUserStatement = connection.prepareStatement(selectUser);
                selectUserStatement.setString(1, email);
                ResultSet rs = selectUserStatement.executeQuery();

                if (rs.next()) {
                    String hash = rs.getString("hash");

                    if (validatePassword(password, hash)) {
                        UUID sessionUUID = sessionsManager.generateSession(email);
                        resp.getWriter().println(sessionUUID.toString()); // TODO standardizzare le risposte
                    } else {
                        resp.sendError(503); // TODO standardizzare le risposte
                    }
                } else {
                    resp.sendError(502); // TODO standardizzare le risposte
                }
            } catch (SQLException e) {
                resp.sendError(500); // TODO standardizzare le risposte
            } catch (NoSuchAlgorithmException e) {
                resp.sendError(500); // TODO standardizzare le risposte
            } catch (InvalidKeySpecException e) {
                resp.sendError(500); // TODO standardizzare le risposte
            }
        }
    }
}
