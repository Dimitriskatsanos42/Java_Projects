package xe2281;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class Concurrency {
    private String coin;
    private String description;
    private int conc;

    public Concurrency(String c, String d, int cn) {
        this.coin = c;
        this.description = d;
        this.conc = cn;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCoin() {
        return this.coin;
    }

    public int getConcurrency() {
        return this.conc;
    }
}

@WebServlet("/CurrencyConverter")
public class CurrencyConverter extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static List<Concurrency> coins = new ArrayList<Concurrency>();

    static {
        coins.add(new Concurrency("EUR", "Ευρώ", 100));
        coins.add(new Concurrency("USD", "Δολάριο ΗΠΑ", 106));
        coins.add(new Concurrency("GBP", "Λίρα Αγγλιας", 84));
    }

    public CurrencyConverter() {
        super();
    }

    private int getConcurrency(String id) {
        for (Concurrency c : this.coins) {
            if (c.getDescription().equals(id)) {
                return c.getConcurrency();
            }
        }
        return -1;
    }

    private void fillFromSelect(PrintWriter pw) {
        for (int i = 0; i < coins.size(); i++) {
            pw.println("<option value=\"" + coins.get(i).getDescription() + "\">" + coins.get(i).getDescription() + "</option>");
        }
    }

    private void fillToSelect(PrintWriter pw) {
        for (int i = 0; i < coins.size(); i++) {
            pw.println("<option value=\"" + coins.get(i).getDescription() + "\">" + coins.get(i).getDescription() + "</option>");
        }
    }

    private double convert(String from, String to, double amount) {
        if (from.equals(to)) {
            return amount;
        }
        int fromTransaction = this.getConcurrency(from);
        int toTransaction = this.getConcurrency(to);
        return (amount * toTransaction) / fromTransaction;
    }

    private String findCoin(String id) {
        for (Concurrency c : this.coins) {
            if (c.getDescription().equals(id)) {
                return c.getCoin();
            }
        }
        return "";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        String fromValue = null, toValue = null, amountInput = null;
        PrintWriter pw = response.getWriter();
        amountInput = request.getParameter("price");
        fromValue = request.getParameter("from");
        toValue = request.getParameter("to");
        pw.println("<html><body style=\"background-color: white;\">");
        pw.println("<center><h2>Μετατροπή Ποσών σε Διαφορετικά Νομίσματα</h2><br>");
        String form = "<form><center>Ποσό:<input type=\"text\" name=\"price\" value=\"" + (amountInput != null ? amountInput : "") + "\"/>"
                + "<label for=\"from\">Από:</label><select name=\"from\" id=\"from\">";
        pw.println(form);
        this.fillFromSelect(pw);
        pw.println("</select>");
        pw.println("<label for=\"to\">Σε:</label><select name=\"to\" id=\"to\">");
        this.fillToSelect(pw);
        pw.println("</select>");
        pw.println("<input type=\"submit\" value=\"Μετατροπή\"/>");
        pw.println("</form>");
        pw.println("<br><br><br><center>");
        boolean checkInput = true;
        double amount = 0;
        try {
            amount = Double.parseDouble(amountInput);
        } catch (NumberFormatException ne) {
            checkInput = false;
        }
        // checkpoint for amount
        if (!checkInput) {
            pw.println("<textarea id=\"res\" name=\"res\" style=\"width=30%; border:2px solid; text-align:center; color:red;\" rows=\"1\" cols=\"50\">Παρακαλώ εισάγεται έναν πραγματικό αριθμό</textarea>");
            pw.println("</body></html>");
            return;
        }
        double result = convert(fromValue, toValue, amount);
        String coin = this.findCoin(toValue);
        String sResult = String.format("Αποτελέσματα:%.3f %s", result, coin);
        pw.println("<textarea id=\"res\" name=\"res\" style=\"width=30%; border:none; text-align:center; color:red;\" rows=\"1\" cols=\"50\">" + sResult + "</textarea>");
        pw.println("</center></body></html>");
    }
}
