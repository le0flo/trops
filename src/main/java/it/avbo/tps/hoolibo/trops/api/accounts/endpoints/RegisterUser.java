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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@WebServlet("/account/register")
public class RegisterUser extends HttpServlet {

    public static final int SALT_BYTES = 32;
    public static final int HASH_BYTES = 32;
    private final int ITERATIONS = 65536;

    private String selectUser = "SELECT hash FROM accounts WHERE email = ?";
    private String insertUser = "INSERT INTO accounts (tipo,email,hash,nome,cognome,data_nascita,cod_fis) VALUES (?,?,?,?,?,?,?)";

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

        Map<String, String> postData = Utilities.readPost(req.getReader());

        String email = postData.get("email");
        String password = postData.get("password");
        String nome = postData.get("nome");
        String cognome = postData.get("cognome");
        String data_nascita = postData.get("data_nascita");
        String cod_fis = postData.get("cod_fis");

        Pattern email_regex = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Pattern password_regex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        Pattern cod_fis_regex = Pattern.compile("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");

        try (Connection connection = ConnectionFactory.getConnection(); PreparedStatement selectUserStatement = connection.prepareStatement(selectUser); PreparedStatement insertUserStatement = connection.prepareStatement(insertUser)) {
            if (email == null || password == null || nome == null || cognome == null || data_nascita == null || cod_fis == null) {
                writer.println(response.errorNullFields());
                resp.setStatus(500);
            } else if (email_regex.matcher(email).find() && password_regex.matcher(password).find() && cod_fis_regex.matcher(cod_fis.toUpperCase()).find()) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d");

                selectUserStatement.setString(1, email);

                if (selectUserStatement.executeQuery().next()) {
                    writer.println(response.errorAlreadyRegistered());
                    resp.setStatus(500);
                } else {
                    String tipo = "USR";
                    if (email.endsWith("@aldini.istruzioneer.it")) {
                        tipo = "STU";
                    } else if (email.endsWith("@avbo.it")) {
                        tipo = "DOC";
                    }

                    String hash = getPasswordHash(password);

                    insertUserStatement.setString(1, tipo);
                    insertUserStatement.setString(2, email);
                    insertUserStatement.setString(3, hash);
                    insertUserStatement.setString(4, nome);
                    insertUserStatement.setString(5, cognome);
                    insertUserStatement.setDate(6, Date.valueOf(LocalDate.parse(data_nascita, df)));
                    insertUserStatement.setString(7, cod_fis.toUpperCase());
                    insertUserStatement.execute();

                    UUID sessionUUID = SessionsManager.getInstance().generateSession(email);

                    JSONObject values = new JSONObject();
                    values.put("uuid-sessione", sessionUUID);
                    values.put("tipo", tipo);

                    writer.println(response.success(values));
                }
            } else {
                writer.println(response.errorInvalidFieldFormat());
                resp.setStatus(500);
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.handleException(e, this.getClass());
            resp.setStatus(500);
        }
    }
}
