package st2281;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/studentsnew")
public class StudentsNew extends HttpServlet {

    private String driver = "org.sqlite.JDBC";
    private String dbURL = "jdbc:sqlite:C:/TED/workspace/eclipse/2281/st2281/src/main/webapp/WEB-INF/teddb.db";

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String qry = "SELECT id, first_name, last_name, semester, email FROM studentsnew";
        String[] columns = new String[]{"id", "first_name", "last_name", "semester", "email"};
        String[] columnsVisible = new String[]{"ΑΜ", "ΟΝΟΜΑ", "ΕΠΩΝΥΜΟ", "ΕΞΑΜΗΝΟ", "EMAIL"};

        Connection dbCon = null;
        Statement stmt = null;
        ResultSet rs = null;

        res.setContentType("text/html");
        res.setCharacterEncoding("utf-8");
        PrintWriter out = res.getWriter();

        try {
            Class.forName(driver);
            dbCon = DriverManager.getConnection(dbURL);
            stmt = dbCon.createStatement();
            rs = stmt.executeQuery(qry);

            out.println("<!DOCTYPE html><html><body>");

            if ("true".equals(req.getParameter("edit")) && req.getParameter("am") != null) {
            	printUpdateForm(out, req.getParameter("am"), dbCon);
            } else {
            	printInsertForm(out);
                printAnyError(out, req);

                // Printing the table
                out.println("<hr/><table border=1><tr>");
                for (String columnVisible : columnsVisible) {
                    out.print("<td><b>");
                    out.print(columnVisible.toUpperCase());
                    out.print("</b></td>");
                }

                while (rs.next()) {
                    out.println("<tr>");
                    for (String column : columns) {
                        out.println("<td>");
                        out.println(rs.getString(column));
                        out.println("</td>");
                    }
                    // Edit and Delete buttons for each student
                    out.println("<td>");
                    out.println("<form action=\"studentsnew\" method=\"POST\" style=\"display:inline;\">");
                    out.println("<input type=\"submit\" name=\"action\" value=\"Edit\" />");
                    out.println("<input type=\"hidden\" name=\"am\" value=\"" + rs.getString("id") + "\">");
                    out.println("</form>");
                    out.println("<form action=\"studentsnew\" method=\"POST\" style=\"display:inline;\">");
                    out.println("<input type=\"submit\" name=\"action\" value=\"Delete\" />");
                    out.println("<input type=\"hidden\" name=\"am\" value=\"" + rs.getString("id") + "\">");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }

            out.println("</body></html>");

        } catch (Exception e) {
            out.println(e.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (dbCon != null) dbCon.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            out.close();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    	
    	req.setCharacterEncoding("utf-8");

        String action = req.getParameter("action");
        Connection dbCon = null;
        PreparedStatement stmt = null;
       

        try {
            Class.forName(driver);
            dbCon = DriverManager.getConnection(dbURL);

            if ("Edit".equals(action)) {
                String am = req.getParameter("am");
                res.sendRedirect("studentsnew?edit=true&am=" + am);
            } else if ("Delete".equals(action)) {
                String am = req.getParameter("am");
                String deleteQry = "DELETE FROM studentsnew WHERE id = ?";
                stmt = dbCon.prepareStatement(deleteQry);
                stmt.setString(1, am);
                stmt.executeUpdate();
                res.sendRedirect("studentsnew");
            } else if ("Update".equals(action)) {
                String updateQry = "UPDATE studentsnew SET first_name=?, last_name=?, semester=?, email=? WHERE id=?";
                stmt = dbCon.prepareStatement(updateQry);
                stmt.setString(1, req.getParameter("onoma"));
                stmt.setString(2, req.getParameter("eponimo"));
                stmt.setString(3, req.getParameter("examino"));
                stmt.setString(4, req.getParameter("email"));
                stmt.setString(5, req.getParameter("am"));
                stmt.executeUpdate();
                res.sendRedirect("studentsnew");
            } else {
                String insertQry = "INSERT INTO studentsnew (id, first_name, last_name, semester, email) VALUES (?, ?, ?, ?, ?)";
                stmt = dbCon.prepareStatement(insertQry);
                stmt.setString(1, req.getParameter("am"));
                stmt.setString(2, req.getParameter("onoma"));
                stmt.setString(3, req.getParameter("eponimo"));
                stmt.setString(4, req.getParameter("examino"));
                stmt.setString(5, req.getParameter("email"));
                stmt.executeUpdate();
                res.sendRedirect("studentsnew");
            }

        } catch (Exception e) {
            res.sendRedirect("studentsnew?errormsg=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));

        } finally {
            try {
                if (stmt != null) stmt.close();
                if (dbCon != null) dbCon.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }


    private void printInsertForm(PrintWriter out) {
        // Κώδικας για την εμφάνιση της φόρμας εισαγωγής
        out.println("<form action=\"studentsnew\" method=\"POST\">");
        out.println("<b> Παρακαλώ δώστε τα ακόλουθα στοιχεία: </b> <br>");
        out.println("<b> Όνομα :  </b> <input type=\"text\" name=\"onoma\" ><br>");
        out.println("<b> Επώνυμο :  </b> <input type=\"text\" name=\"eponimo\" ><br>");
        out.println("<b> Αριθμός Μητρώου: </b> <input type=\"text\" name=\"am\" ><br>");
        out.println("<b> Εξάμηνο: </b> <input type=\"text\" name=\"examino\" ><br>");
        out.println("<b> Email: </b> <input type=\"text\" name=\"email\" ><br>");
        out.println("<input type=\"submit\" value=\"Insert\">");
        out.println("</form>");
    }

    private void printUpdateForm(PrintWriter out, String am, Connection dbCon) throws SQLException {
        PreparedStatement stmt = dbCon.prepareStatement("SELECT * FROM studentsnew WHERE id = ?");
        stmt.setString(1, am);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            out.println("<form action=\"studentsnew\" method=\"POST\">");
            out.println("<b>Παρακαλώ δώστε τα ακόλουθα στοιχεία: </b><br>");
            out.println("<b>Όνομα: </b> <input type=\"text\" name=\"onoma\" value=\"" + rs.getString("first_name") + "\"><br>");
            out.println("<b>Επώνυμο: </b> <input type=\"text\" name=\"eponimo\" value=\"" + rs.getString("last_name") + "\"><br>");
            out.println("<b>Αριθμός Μητρώου (ΑΜ): </b> <input type=\"text\" name=\"am\" value=\"" + rs.getString("id") + "\" readonly><br>");
            out.println("<b>Εξάμηνο: </b> <input type=\"text\" name=\"examino\" value=\"" + rs.getString("semester") + "\"><br>");
            out.println("<b>Email: </b> <input type=\"text\" name=\"email\" value=\"" + rs.getString("email") + "\"><br>");
            out.println("<input type=\"submit\" name=\"action\" value=\"Update\">");
            out.println("</form>");
        }
        rs.close();
        stmt.close();
    }

    private void printAnyError(PrintWriter out, HttpServletRequest req) {
        String errorMessage = req.getParameter("errormsg");
        if (errorMessage != null) {
            out.println("<br><strong style=\"color:red\">Error: " + errorMessage + "</strong><br>");
        }
    }
}
