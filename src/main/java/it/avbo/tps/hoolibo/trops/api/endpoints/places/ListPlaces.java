package it.avbo.tps.hoolibo.trops.api.endpoints.places;

import it.avbo.tps.hoolibo.trops.api.database.dao.PlaceDAO;
import it.avbo.tps.hoolibo.trops.api.managers.ResponseManager;
import it.avbo.tps.hoolibo.trops.api.utils.GeneralUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/places/list")
public class ListPlaces extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        ResponseManager response = ResponseManager.getInstance();

        try {
            String authorization = req.getHeader("Authorization");
            String account = GeneralUtils.checkSession(authorization);

            if (account == null) {
                response.errorInvalidSessionUUID(resp);
            } else {
                response.successArray(resp, PlaceDAO.getInstance().fetchAll());
            }
        } catch (IllegalArgumentException | SQLException e) {
            response.handleException(resp, e, this.getClass());
        }
    }
}
