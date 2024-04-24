package it.avbo.tps.hoolibo.trops.api.endpoints.accounts;

import it.avbo.tps.hoolibo.trops.api.database.dao.UserDAO;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.managers.SessionsManager;
import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@WebServlet("/account/register")
public class RegisterUser extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();
        Map<String, String> requestBodyMap = GeneralUtils.readPost(req.getReader());

        String email = requestBodyMap.get("email");
        String password = requestBodyMap.get("password");
        String nome = requestBodyMap.get("nome");
        String cognome = requestBodyMap.get("cognome");
        String data_nascita = requestBodyMap.get("data_nascita");
        String cod_fis = requestBodyMap.get("cod_fis");

        Pattern email_regex = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Pattern password_regex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        Pattern cod_fis_regex = Pattern.compile("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");

        try {
            if (email == null || password == null || nome == null || cognome == null || data_nascita == null || cod_fis == null) {
                response.errorNullFields(resp);
            } else if (email_regex.matcher(email).find() && password_regex.matcher(password).find() && cod_fis_regex.matcher(cod_fis.toUpperCase()).find()) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d");
                Date data = Date.valueOf(LocalDate.parse(data_nascita, df));

                boolean success = UserDAO.getInstance().insert(email, password, nome, cognome, data, cod_fis);

                if (success) {
                    UUID sessionUUID = SessionsManager.getInstance().generateSession(email);

                    JSONObject values = new JSONObject();
                    values.put("uuid-sessione", sessionUUID);

                    response.success(resp, values);
                } else {
                    response.errorAlreadyRegistered(resp);
                }
            } else {
                response.errorInvalidFieldFormat(resp);
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
