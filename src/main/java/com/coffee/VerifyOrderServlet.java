package com.coffee;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/verify-order")
public class VerifyOrderServlet extends HttpServlet {

    @Override
    protected void doGet(
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

        String sql =
            "SELECT " +
            "customer_name, " +
            "GROUP_CONCAT(" +
            "CONCAT(coffee_name, ' x ', quantity) " +
            "SEPARATOR ', ')" +
            " AS coffee_list, " +
            "SUM(quantity) AS total_quantity, " +
            "SUM(total_price) AS subtotal, " +
            "payment_status, " +
            "order_status " +
            "FROM orders " +
            "WHERE order_id = ? " +
            "GROUP BY customer_name, payment_status, order_status";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    double subtotal =
                            rs.getDouble("subtotal");

                    double gst =
                            subtotal * 0.05;

                    double total =
                            subtotal + gst;

                    resp.getWriter().print(
                        "{"
                        + "\"success\":true,"
                        + "\"orderId\":\""
                        + escape(orderId)
                        + "\","
                        + "\"customerName\":\""
                        + escape(rs.getString("customer_name"))
                        + "\","
                        + "\"coffeeName\":\""
                        + escape(rs.getString("coffee_list"))
                        + "\","
                        + "\"quantity\":"
                        + rs.getInt("total_quantity")
                        + ","
                        + "\"subtotal\":"
                        + subtotal
                        + ","
                        + "\"gst\":"
                        + gst
                        + ","
                        + "\"total\":"
                        + total
                        + ","
                        + "\"paymentStatus\":\""
                        + escape(rs.getString("payment_status"))
                        + "\","
                        + "\"orderStatus\":\""
                        + escape(rs.getString("order_status"))
                        + "\""
                        + "}"
                    );

                } else {

                    resp.setStatus(404);

                    resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Order not found.\"}"
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(500);

            resp.getWriter().print(
                "{\"success\":false,\"message\":\"Could not verify order.\"}"
            );
        }
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }
}