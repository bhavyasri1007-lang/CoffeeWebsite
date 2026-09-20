package com.coffee;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String customerName = req.getParameter("customerName");
        String customerEmail = req.getParameter("customerEmail");

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            customerEmail = req.getParameter("email");
        }

        String coffeeName = req.getParameter("itemName");

        if (coffeeName == null || coffeeName.trim().isEmpty()) {
            coffeeName = req.getParameter("coffeeName");
        }

        String quantityText = req.getParameter("quantity");
        String totalText = req.getParameter("total");

        if (isBlank(customerName)
                || isBlank(customerEmail)
                || isBlank(coffeeName)
                || isBlank(quantityText)
                || isBlank(totalText)) {

            resp.setStatus(400);

            resp.getWriter().print(
                "{\"success\":false,\"message\":\"All fields are required.\"}"
            );

            return;
        }

        try {

            int quantity = Integer.parseInt(quantityText);
            double totalPrice = Double.parseDouble(totalText);

            if (quantity < 1 || totalPrice < 0) {
                throw new NumberFormatException();
            }

            String sql =
                "INSERT INTO orders " +
                "(customer_name, customer_email, coffee_name, quantity, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";

            try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
            ) {

                ps.setString(1, customerName);
                ps.setString(2, customerEmail);
                ps.setString(3, coffeeName);
                ps.setInt(4, quantity);
                ps.setDouble(5, totalPrice);

                ps.executeUpdate();
            }

            resp.getWriter().print(
                "{\"success\":true,\"message\":\"Order placed successfully!\"}"
            );

        } catch (NumberFormatException e) {

            resp.setStatus(400);

            resp.getWriter().print(
                "{\"success\":false,\"message\":\"Invalid quantity or total.\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(500);

            resp.getWriter().print(
                "{\"success\":false,\"message\":\"Could not place order.\"}"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
