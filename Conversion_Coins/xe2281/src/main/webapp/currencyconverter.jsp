<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Currency Converter</title>
    <style>
        body {
            text-align: center;
            font-family: Arial, sans-serif;
        }
        form {
            display: inline-block;
            text-align: left;
            margin-top: 20px;
        }
        h1 {
            font-size: 24px;
            font-weight: bold;
        }
        h2 {
            font-size: 13px;
            font-weight: bold;
            color: red;
            border: 1px solid #000;
            padding: 5px;
            display: inline-block;
            margin-top: 10px;
        }
        div#resultContainer {
            margin-top: 15px;
        }
    </style>
</head>
<body>
    <h1>Μετατροπή Ποσών σε Διαφορετικά Νομίσματα</h1>

    <%
        double EUR_TO_USD = 1.06;
        double EUR_TO_GBP = 0.84;

        String amountStr = request.getParameter("amount");
        String fromCurrency = request.getParameter("from");
        String toCurrency = request.getParameter("to");

        boolean hasError = false;

        double amount = 0.0;
        double result = 0.0;

        if (amountStr != null && fromCurrency != null && toCurrency != null) {
            try {
                amount = Double.parseDouble(amountStr);

                if (fromCurrency.equals("EUR")) {
                    if (toCurrency.equals("USD")) {
                        result = amount * EUR_TO_USD;
                    } else if (toCurrency.equals("GBP")) {
                        result = amount * EUR_TO_GBP;
                    } else {
                        result = amount;
                    }
                } else if (fromCurrency.equals("USD")) {
                    if (toCurrency.equals("EUR")) {
                        result = amount / EUR_TO_USD;
                    } else if (toCurrency.equals("GBP")) {
                        result = (amount / EUR_TO_USD) * EUR_TO_GBP;
                    } else {
                        result = amount;
                    }
                } else if (fromCurrency.equals("GBP")) {
                    if (toCurrency.equals("EUR")) {
                        result = amount / EUR_TO_GBP;
                    } else if (toCurrency.equals("USD")) {
                        result = (amount / EUR_TO_GBP) * EUR_TO_USD;
                    } else {
                        result = amount;
                    }
                } else {
                    result = amount;
                }
            } catch (NumberFormatException e) {
                hasError = true;
            }
        }
    %>

    <form method="get">
        <label for="amount">Ποσό:</label>
        <input type="text" name="amount" value="<%= (amountStr != null ? amountStr : "") %>">
        <label for="from">Από:</label>
        <select name="from">
            <option value="EUR" <%= (fromCurrency != null && fromCurrency.equals("EUR") ? "selected" : "") %>>Ευρώ</option>
            <option value="USD" <%= (fromCurrency != null && fromCurrency.equals("USD") ? "selected" : "") %>>Δολάρια</option>
            <option value="GBP" <%= (fromCurrency != null && fromCurrency.equals("GBP") ? "selected" : "") %>>Λίρες</option>
        </select>
        <label for="to">Σε:</label>
        <select name="to">
            <option value="EUR" <%= (toCurrency != null && toCurrency.equals("EUR") ? "selected" : "") %>>Ευρώ</option>
            <option value="USD" <%= (toCurrency != null && toCurrency.equals("USD") ? "selected" : "") %>>Δολάρια</option>
            <option value="GBP" <%= (toCurrency != null && toCurrency.equals("GBP") ? "selected" : "") %>>Λίρες</option>
        </select>
        <input type="submit" value="Μετατροπή">
    </form>

    <% if (hasError) { %>
        <h2>Παρακαλώ εισάγετε έγκυρο αριθμό για το ποσό.</h2>
    <% } else if (amountStr != null && fromCurrency != null && toCurrency != null) { %>
        <div id="resultContainer">
            <h2>Αποτέλεσμα: <%= amount %> <%= fromCurrency %> = <%= result %> <%= toCurrency %></h2>
        </div>
    <% } %>

</body>
</html>
