package xe2281; // Always use packages. Never use default package.

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Servlet implementation class CurrencyConverter
 */
@WebServlet("/CurrencyConverter2")
public class CurrencyConverter2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PrintWriter pw;
    private static List <currency> inf=new ArrayList<currency>();
    private void  read_data()
    {
    	if(inf.size()!=0) {return;}
    	try {
			BufferedReader br=new BufferedReader(new InputStreamReader (new FileInputStream(
					getServletContext().getRealPath("/WEB-INF/euro_rates.txt")),"UTF-8"));
			try {
				String line=br.readLine();
				while(line!=null)
				{
					String data[]=line.split(",");
					inf.add(new currency(data[0],data[1],Integer.parseInt(data[2])));
					line=br.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (UnsupportedEncodingException e) {
		} catch (FileNotFoundException e) {
		}
    }
    public CurrencyConverter2() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void fill_selection()
    {
    	for(currency c:inf)
    	{
    		pw.println("<option value=\""+c.getType()+"\">"+c.getType()+"</option>");
    	}
    }
    
    private int getConc(String title)
    {
    	for(currency c:inf)
    	{
    		if(c.getType().equals(title))
    		{
    			return c.getAmount();
    		}
    	}
    	return -1;
    }
    
    private String getType(String title)
    {
    	for(currency c:inf)
    	{
    		if(c.getType().equals(title))
    		{
    			return c.getId();
    		}
    	}
    	return "";
    }
    
    private void convert(String from, String to, double am) {
        int fr = this.getConc(from);
        int t = this.getConc(to);
        double res = (am * t) / fr;
        String result = String.format("Αποτελέσματα: %.3f %s", res, this.getType(to));
        
        
        String colorStyle = "color:red;";
        pw.println("<center><textarea id=\"res\" rows=\"2\" cols=\"50\" style=\"text-align:center; " + colorStyle + "\">" + result + "</textarea></center>");
    }


    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        pw=response.getWriter();
        this.read_data();
        String fromvalue=request.getParameter("from");
        String tovalue=request.getParameter("to");
        String inputammount=request.getParameter("amount");
        pw.println("<html><body style=\"background-color:;\"><center><h2>Μετατροπή Ποσών σε Διαφορετικά Νομίσματα</h2></center><form>");
        pw.println("<center><label for=\"input\">Ποσό:</label>");
        pw.println("<input type=\"text\" name=\"amount\" value=\""+(inputammount!=null?inputammount:"0.0")+"\"/>");
        pw.println("<label for=\"from\">Από:</label><select id=\"from\" name=\"from\">");
        this.fill_selection();
        pw.println("</select><label for=\"to\">Σε:</label><select name=\"to\" id=\"to\">");
        this.fill_selection();
        pw.println("</select><input type=\"submit\" value=\"Μετατροπή\"/></form></center><br><br><br>");
        boolean checkinput=true;
        BigDecimal moneyamount=null;
        try {
            moneyamount=new BigDecimal(Double.parseDouble(inputammount));    
        } catch(NumberFormatException ne) {
            checkinput=false;
        }
        if(!checkinput) {
            pw.println("<center><strong><textarea id=\"res\" rows=\"2\" cols=\"50\">Παρακαλώ εισάγεται Κανονικό ποσό για μετατροπή</textarea></strong>");
            pw.println("</body></html>");
            return;
        }
        this.convert(fromvalue, tovalue, moneyamount.doubleValue());
        pw.println("</body></html>");
    }

}

