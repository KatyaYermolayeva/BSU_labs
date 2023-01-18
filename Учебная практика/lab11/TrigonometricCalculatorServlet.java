package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TrigonometricCalculatorServlet extends HttpServlet {
   @Override
   public void doGet( HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      resp.sendRedirect("index.html");
    }
   @Override
    public void doPost( HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String angle = req.getParameter("angle");
        String function = req.getParameter("function");
        String measure = req.getParameter("measure");
        String accuracy = req.getParameter("accuracy");
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        try {
            double x = Double.parseDouble(angle);
            if (measure.equals("degrees")){
                x = Math.toRadians(x);
            }
            double result = 0;
            switch(function){
                case "cos":{
                    result = Math.cos(x);
                    break;
                }
                case "sin":{
                    result = Math.sin(x);
                    break;
                }
                case "tg":{
                    result = Math.tan(x);
                    break;
                }
                case "ctg":{
                    result = 1.0 / Math.tan(x);
                    break;
                }
                case "sec":{
                    result = 1.0 / Math.cos(x);
                    break;
                }
                case "cosec":{
                    result = 1.0 / Math.sin(x);
                    break;
                }
            }
            writer.println(String.format("<h2>%s(%s)=%." + accuracy + "f</h2>",
                    function, angle, result));
        }
        catch(NumberFormatException e){
            writer.println("<h2>Invalid angle value.</h2>");
        }
        catch(Exception e){
            writer.println("<h2>Unable to calculate " + function +
                    "(" + angle + ")" + ".</h2>");
        }
        finally {
            writer.close();  
        }
    }
}
