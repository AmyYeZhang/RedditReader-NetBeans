package view;

import common.ValidationException;
import entity.Host;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.HostLogic;
import logic.LogicFactory;

/**
 * The Class CreateHost is to add new Host.
 * @author Ziyue Wang 040919399
 * @author Ye Zhang   040958453
 *  
 */
@WebServlet(name = "CreateHost", urlPatterns = {"/CreateHost"})
public class CreateHost extends HttpServlet {

    private String errorMessage = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            //output the page for adding new Host
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Host</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");

            HostLogic logic = LogicFactory.getFor("Host");
            for(String name : logic.getColumnCodes()){
                if(name != HostLogic.ID && name != HostLogic.EXTRACTION_TYPE){
                    out.printf("%s:<br>\n", name);
                    out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",name);
                    out.println("<br>");
                }else if(name.equals(HostLogic.EXTRACTION_TYPE)){
                    //EXTRACTION_TYPE for Host is limited in the dropdown list
                    out.printf("%s:<br>\n", name);
                    out.printf("<select name=\"%s\" id=\"host\" style=\"width: 170px\">", name);
                    out.printf("<option value=\"json\">json</option>");
                    out.printf("<option value=\"html\">html</option>");
                    out.printf("<option value=\"xml\">xml</option>");
                    out.println("</select><br><br>");
                }
            }

            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add Host\">");
            out.println("</form>");
            if(errorMessage!=null&&!errorMessage.isEmpty()){
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * get method is called first when requesting a URL. since this servlet
     * will create a host this method simple delivers the html code. 
     * creation will be done in doPost method.
     *
     * @param request 
     * @param response 
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        errorMessage = null;
        
        HostLogic hLogic = LogicFactory.getFor("Host");
        String name = request.getParameter(HostLogic.NAME);
        
        //check the host name is unique
        if(hLogic.getHostWithName(name)==null){
            try{
                //create a Host and add to database
                Host host = hLogic.createEntity( request.getParameterMap());
                hLogic.add(host);
            }catch(ValidationException ex){
                errorMessage = ex.getMessage();
            }
            
        }else{
            //if host name is duplicated, print the error message
            errorMessage = "Name: \"" + name + "\" already exists";
        }
        if( request.getParameter("add")!=null){
            //if add button is pressed return the same page
            processRequest(request, response);
        }else if (request.getParameter("view")!=null) {
            //if view button is pressed redirect to the Host table
            response.sendRedirect("HostTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Host Entity";
    }

    private static final boolean DEBUG = true;

    public void log( String msg) {
        if(DEBUG){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log( message);
        }
    }

    public void log( String msg, Throwable t) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log( message, t);
    }
}
