package com.coffee;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/rating")
public class RatingServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String customerName =
                req.getParameter("customerName");

        String ratingText =
                req.getParameter("rating");

        String feedback =
                req.getParameter("feedback");


        // Check required fields

        if (customerName == null
                || customerName.trim().isEmpty()
                || ratingText == null
                || ratingText.trim().isEmpty()) {

            resp.setStatus(400);

            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Please enter your name and rating.\"}"
            );

            return;
        }


        try {

            int rating =
                    Integer.parseInt(ratingText);


            // Rating must be between 1 and 5

            if (rating < 1 || rating > 5) {

                resp.setStatus(400);

                resp.getWriter().print(
                        "{\"success\":false,\"message\":\"Rating must be between 1 and 5.\"}"
                );

                return;
            }


            String sql =
                    "INSERT INTO ratings " +
                    "(customer_name, rating, feedback) " +
                    "VALUES (?, ?, ?)";


            try (
                    Connection con =
                            DBConnection.getConnection();

                    PreparedStatement ps =
                            con.prepareStatement(sql)
            ) {

                ps.setString(1, customerName);
                ps.setInt(2, rating);
                ps.setString(3, feedback);

                ps.executeUpdate();
            }


            resp.getWriter().print(
                    "{\"success\":true,\"message\":\"Thank you for your rating!\"}"
            );


        } catch (NumberFormatException e) {

            resp.setStatus(400);

            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Invalid rating.\"}"
            );


        } catch (Exception e) {

            e.printStackTrace();

            resp.setStatus(500);

            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Could not save your rating.\"}"
            );
        }
    }
}