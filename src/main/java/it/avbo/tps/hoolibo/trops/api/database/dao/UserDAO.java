package it.avbo.tps.hoolibo.trops.api.database.dao;

import it.avbo.tps.hoolibo.trops.api.database.ConnectionFactory;
import it.avbo.tps.hoolibo.trops.api.utils.PasswordUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.LocalDate;

public class UserDAO {

    private static UserDAO instance = null;

    private String selectUser, insertUser;

    public UserDAO() {
        selectUser = "SELECT tipo, hash, nome, cognome, data_nascita, cod_fis FROM accounts WHERE email = ?";
        insertUser = "INSERT INTO accounts (tipo,email,hash,nome,cognome,data_nascita,cod_fis) VALUES (?,?,?,?,?,?,?)";
    }

    public boolean insert(String email, String password, String nome, String cognome, Date data_nascita, String cod_fis) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement selectUserStmt = conn.prepareStatement(selectUser);
        PreparedStatement insertUserStmt = conn.prepareStatement(insertUser);

        selectUserStmt.setString(1, email);
        ResultSet rs = selectUserStmt.executeQuery();

        if (rs.next()) {
            return false;
        } else {
            String tipo = "USR";
            if (email.endsWith("@aldini.istruzioneer.it")) {
                tipo = "STU";
            } else if (email.endsWith("@avbo.it")) {
                tipo = "DOC";
            }

            String hash = PasswordUtils.hash(password);

            insertUserStmt.setString(1, tipo);
            insertUserStmt.setString(2, email);
            insertUserStmt.setString(3, hash);
            insertUserStmt.setString(4, nome);
            insertUserStmt.setString(5, cognome);
            insertUserStmt.setDate(6, data_nascita);
            insertUserStmt.setString(7, cod_fis.toUpperCase());
            insertUserStmt.execute();

            return true;
        }
    }

    public String selectHash(String email) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        PreparedStatement selectUserStmt = conn.prepareStatement(selectUser);

        selectUserStmt.setString(1, email);
        ResultSet rs = selectUserStmt.executeQuery();

        if (rs.next()) {
            return rs.getString("hash");
        } else {
            return null;
        }
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }

        return instance;
    }
}
