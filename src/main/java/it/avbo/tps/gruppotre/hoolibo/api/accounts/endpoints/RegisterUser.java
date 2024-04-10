package it.avbo.tps.gruppotre.hoolibo.api.accounts.endpoints;

import it.avbo.tps.gruppotre.hoolibo.api.ConnectionFactory;
import it.avbo.tps.gruppotre.hoolibo.api.accounts.managers.ResponseManager;
import it.avbo.tps.gruppotre.hoolibo.api.accounts.managers.SessionsManager;
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
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

@WebServlet("/account/register")
public class RegisterUser extends HttpServlet {

    public static final int SALT_BYTES = 32;
    public static final int HASH_BYTES = 32;
    private final int ITERATIONS = 65536;

    private String selectUser = "SELECT hash FROM accounts WHERE email = ?";
    private String insertUser = "INSERT INTO accounts (email,hash,nome,cognome,data_nascita,cod_fis,cod_scuola) VALUES (?,?,?,?,?,?,?)";

    private String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();

        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
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
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        PrintWriter writer = resp.getWriter();

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String nome = req.getParameter("nome");
        String cognome = req.getParameter("cognome");
        String data_nascita = req.getParameter("data_nascita");
        String cod_fis = req.getParameter("cod_fis");
        String cod_scuola = req.getParameter("cod_scuola");

        Pattern cod_fis_regex = Pattern.compile("/^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$/i");
        Pattern email_regex = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Pattern password_regex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");

        try (Connection connection = ConnectionFactory.getConnection(); PreparedStatement selectUserStatement = connection.prepareStatement(selectUser); PreparedStatement insertUserStatement = connection.prepareStatement(insertUser)) {
            if (email == null || password == null || nome == null || cognome == null) {
                writer.println(response.errorNullFields());
            } else if (cod_fis_regex.matcher(cod_fis).find() && email_regex.matcher(email).find() && password_regex.matcher(password).find()) {
                selectUserStatement.setString(1, email);

                if (selectUserStatement.executeQuery().next()) {
                    writer.println(response.errorAlreadyRegistered());
                } else {
                    String hash = getPasswordHash(password);

                    insertUserStatement.setString(1, email);
                    insertUserStatement.setString(2, hash);
                    insertUserStatement.setString(3, nome);
                    insertUserStatement.setString(4, cognome);
                    insertUserStatement.setString(5, data_nascita);
                    insertUserStatement.setString(6, cod_fis);
                    insertUserStatement.setString(7, cod_scuola);
                    insertUserStatement.execute();

                    UUID sessionUUID = SessionsManager.getInstance().generateSession(email);

                    JSONObject values = new JSONObject();
                    values.put("uuid-sessione", sessionUUID);
                    values.put("tipo", "USR");

                    writer.println(response.success(values));
                }
            } else {
                // TODO messaggio di errore
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            resp.sendError(500);
            response.handleException(e, this.getClass());
        }
    }
}
