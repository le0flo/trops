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
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/register")
public class Register extends HttpServlet {

    public static final int SALT_BYTES = 32;
    public static final int HASH_BYTES = 32;
    private final int ITERATIONS = 65536;

    private String selectUser = "SELECT hash FROM accounts WHERE email = ?";
    private String insertUser = "INSERT INTO accounts (email,hash,nome,cognome) VALUES (?,?,?,?)";

    private String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();

        if(paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        }
        else {
            return hex;
        }
    }

    private String getPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_BYTES * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return ITERATIONS + ":" + toHex(salt) + ":" + toHex(hash);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");

        if (email == null || password == null || nome == null || cognome == null) {
            resp.sendError(501); // TODO standardizzare le risposte
        } else {
            // TODO Implementare quì regex per controllare validità delle varie informazioni

            try (Connection connection = ConnectionFactory.getConnection()) {
                PreparedStatement selectUserStatement = connection.prepareStatement(selectUser);
                selectUserStatement.setString(1, email);

                if (selectUserStatement.executeQuery().next()) {
                    resp.sendError(502); // TODO standardizzare le risposte
                } else {
                    String hash = getPasswordHash(password);

                    PreparedStatement insertUserStatement = connection.prepareStatement(insertUser);
                    insertUserStatement.setString(1, email);
                    insertUserStatement.setString(2, hash);
                    insertUserStatement.setString(3, nome);
                    insertUserStatement.setString(4, cognome);

                    insertUserStatement.execute();

                    UUID sessionUUID = SessionsManager.getInstance().generateSession(email);
                    resp.getWriter().println(sessionUUID.toString()); // TODO standardizzare le risposte
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
