package com.coffee;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/serve-order")
public class ServeOrderServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String orderId = req.getParameter("orderId");

        if (orderId == null || orderId.trim().isEmpty()) {

            resp.setStatus(400);

            resp.getWriter().print(
                "{\"success\":false,\"message\":\"Order ID is required.\"}"
            );

            return;
        }

        String checkSql =
            "SELECT order_status, payment_status " +
            "FROM orders " +
            "WHERE order_id = ? " +
            "LIMIT 1";

        String updateSql =
            "UPDATE orders " +
            "SET order_status = 'SERVED' " +
            "WHERE order_id = ? " +
            "AND payment_status = 'PAID' " +
            "AND order_status = 'READY'";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement checkPs =
                con.prepareStatement(checkSql)
        ) {

            checkPs.setString(1, orderId);

            try (ResultSet rs = checkPs.executeQuery()) {

                if (!rs.next()) {

                    resp.setStatus(404);

                    resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Order not found.\"}"
                    );

                    return;
                }

                String paymentStatus =
                    rs.getString("payment_status");

                String orderStatus =
                    rs.getString("order_status");

                if (!"PAID".equals(paymentStatus)) {

                    resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Payment is not completed.\"}"
                    );

                    return;
                }

                if ("SERVED".equals(orderStatus)) {

                    resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Order already served.\"}"
                    );

                    return;
                }

                if (!"READY".equals(orderStatus)) {

                    resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Order is not ready.\"}"
                    );

                    return;
                }
            }

            try (
                PreparedStatement updatePs =
                    con.prepareStatement(updateSql)
            ) {

                updatePs.setString(1, orderId);

                int updatedRows =
                    updatePs.executeUpdate();

                if (updatedRows > 0) {

                    resp.getWriter().print(
                        "{\"success\":true,\"message\":\"Order served successfully.\"}"
                    );

                } else {

                    resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Order could not be served.\"}"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(500);

            resp.getWriter().print(
                "{\"success\":false,\"message\":\"Could not serve order.\"}"
            );
        }
    }
}