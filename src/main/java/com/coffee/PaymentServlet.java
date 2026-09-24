package com.coffee;

import com.coffee.payment.PaymentService;
import com.coffee.payment.TestPaymentService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/payment")
public class PaymentServlet extends HttpServlet {

    private final PaymentService paymentService =
            new TestPaymentService();

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String amountText = req.getParameter("amount");
        String paymentId = req.getParameter("paymentId");

        // Check required fields
        if (amountText == null
                || amountText.trim().isEmpty()
                || paymentId == null
                || paymentId.trim().isEmpty()) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Amount and payment ID are required.\"}"
            );

            return;
        }

        try {

            double amount = Double.parseDouble(amountText);

            // Basic amount validation
            if (amount <= 0) {

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Invalid payment amount.\"}"
                );

                return;
            }

            // Send payment request to our payment service
            boolean paymentSuccessful =
                    paymentService.processPayment(
                            amount,
                            paymentId
                    );

            if (paymentSuccessful) {

                resp.getWriter().print(
                        "{\"success\":true,\"message\":\"Payment verified successfully.\"}"
                );

            } else {

                resp.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);

                resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Payment verification failed.\"}"
                );
            }

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Invalid amount.\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Payment processing error.\"}"
            );
        }
    }
}