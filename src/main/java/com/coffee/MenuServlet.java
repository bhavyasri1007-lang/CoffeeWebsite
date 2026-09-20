package com.coffee;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/api/menu")
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String sql = "SELECT id, name, description, price FROM menu_items";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            PrintWriter out = resp.getWriter()
        ) {

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{")
                    .append("\"id\":")
                    .append(rs.getInt("id"))
                    .append(",")

                    .append("\"name\":\"")
                    .append(escape(rs.getString("name")))
                    .append("\",")

                    .append("\"description\":\"")
                    .append(escape(rs.getString("description")))
                    .append("\",")

                    .append("\"price\":")
                    .append(rs.getDouble("price"))

                    .append("}");
            }

            json.append("]");

            out.print(json);

        }  catch (SQLException e) {

    e.printStackTrace();

    resp.setStatus(500);

    resp.getWriter().print(
        "{\"error\":\"Database error\"}"
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
