
package com.coffee;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/contact")
public class ContactServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String message = req.getParameter("message");

        if (blank(name) || blank(email) || blank(message)) {
            resp.setStatus(400);
            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Please fill all fields.\"}"
            );
            return;
        }

        String sql = "INSERT INTO messages (name, email, message) VALUES (?, ?, ?)";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, message);

            ps.executeUpdate();

            resp.getWriter().print(
                    "{\"success\":true,\"message\":\"Message sent successfully!\"}"
            );

        } catch (Exception e) {

            resp.setStatus(500);
            resp.getWriter().print(
                    "{\"success\":false,\"message\":\"Could not send message.\"}"
            );
        }
    }

    private boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
