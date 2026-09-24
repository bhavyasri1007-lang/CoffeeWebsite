
package com.coffee;

import com.coffee.payment.PaymentService;
import com.coffee.payment.TestPaymentService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {

    private final PaymentService paymentService =
            new TestPaymentService();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String orderId =
                req.getParameter("orderId");

        String customerName =
                req.getParameter("customerName");

        String customerEmail =
                req.getParameter("customerEmail");

        String coffeeName =
                req.getParameter("coffeeName");

        String quantityText =
                req.getParameter("quantity");

        String totalText =
                req.getParameter("total");

        String paymentId =
                req.getParameter("paymentId");

        if (isBlank(orderId)
                || isBlank(customerName)
                || isBlank(customerEmail)
                || isBlank(coffeeName)
                || isBlank(quantityText)
                || isBlank(totalText)
                || isBlank(paymentId)) {

            resp.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            resp.getWriter().print(
                    "{\"success\":false," +
                    "\"message\":\"All order and payment fields are required.\"}"
            );

            return;
        }

        try {

            int quantity =
                    Integer.parseInt(quantityText);

            double totalPrice =
                    Double.parseDouble(totalText);

            if (quantity < 1
                    || totalPrice <= 0) {

                throw new NumberFormatException();
            }

            /*
             * BACKEND PAYMENT VERIFICATION
             */

            boolean paymentSuccessful =
                    paymentService.processPayment(
                            totalPrice,
                            paymentId
                    );

            if (!paymentSuccessful) {

                resp.setStatus(
                        HttpServletResponse.SC_PAYMENT_REQUIRED
                );

                resp.getWriter().print(
                        "{\"success\":false," +
                        "\"message\":\"Payment verification failed.\"}"
                );

                return;
            }

            /*
             * PAYMENT VERIFIED
             * Now save the order as PAID.
             */

            String sql =
                    "INSERT INTO orders " +
                    "(order_id, customer_name, customer_email, " +
                    "coffee_name, quantity, total_price, " +
                    "payment_method, payment_status, " +
                    "order_status, paid_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

            try (
                    Connection con =
                            DBConnection.getConnection();

                    PreparedStatement ps =
                            con.prepareStatement(sql)
            ) {

                ps.setString(1, orderId);
                ps.setString(2, customerName);
                ps.setString(3, customerEmail);
                ps.setString(4, coffeeName);
                ps.setInt(5, quantity);
                ps.setDouble(6, totalPrice);

                ps.setString(7, "TEST");
                ps.setString(8, "PAID");
                ps.setString(9, "READY");

                ps.executeUpdate();
            }

            resp.getWriter().print(
                    "{\"success\":true," +
                    "\"message\":\"Payment verified and order placed successfully!\"}"
            );

        } catch (NumberFormatException e) {

            resp.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            resp.getWriter().print(
                    "{\"success\":false," +
                    "\"message\":\"Invalid quantity or total.\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.getWriter().print(
                    "{\"success\":false," +
                    "\"message\":\"Could not place order.\"}"
            );
        }
    }

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}