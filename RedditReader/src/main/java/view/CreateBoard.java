package view;

import common.ValidationException;
import entity.Board;
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
import logic.BoardLogic;
import logic.HostLogic;
import logic.LogicFactory;

/**
 * The class CreateBoard is to add new Board.
 * @author Ye Zhang   040958453
 * @author Ziyue Wang 040919399
 * 
 */
@WebServlet(name = "CreateBoard", urlPatterns = {"/CreateBoard"})
public class CreateBoard extends HttpServlet {

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
            //output the page for adding new Board
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Board</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
 
            BoardLogic logic = LogicFactory.getFor("Board");
            for(String name : logic.getColumnCodes()){
                if(!name.equals(BoardLogic.ID) && !name.equals(BoardLogic.HOST_ID)){
                    out.printf("%s:<br>\n", name);
                    out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",name);
                    out.println("<br>");
                }else if(name.equals(BoardLogic.HOST_ID)){
                    //Host for Board is limited in the Host table, use the dropdown list to limit the Host
                    out.printf("%s:<br>\n", name);
                    out.printf("<select name=\"%s\" id=\"host\" style=\"width: 170px\">", name);
                    
                    //get the Host name from Host table, then add Host name to the dropdown list
                    HostLogic hLogic = LogicFactory.getFor("Host");
                    hLogic.getAll().forEach(host ->
                            out.printf("<option value=\"%s\">%s</option>", host.getId(), host.getName())
                    );
                    out.println("</select><br><br>");
                }
            }

            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add Board\">");
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
     * will create a board this method simple delivers the html code. 
     * creation will be done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
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
        
        BoardLogic bLogic = LogicFactory.getFor("Board");
        String url = request.getParameter(BoardLogic.URL);
        
        if(bLogic.getBoardWithUrl(url)==null){
            try{
                HostLogic hLogic = LogicFactory.getFor("Host");
                Board board = bLogic.createEntity(request.getParameterMap());
                int hostId = Integer.parseInt(request.getParameter(BoardLogic.HOST_ID));
                
                //check the Host is exist
                Host host = hLogic.getWithId(hostId);
                if(host == null){
                    //if Host is not exist, print the error message
                    errorMessage = "Host ID: \"" + hostId + "\" does not exist";
                }else{
                    //if Host is exist, add the Board to database
                    board.setHostid(host);
                    bLogic.add(board);
                }
            }catch(ValidationException ex){
                errorMessage = ex.getMessage();
            }
        }else{
            //if url is duplicated, print the error message
            errorMessage = "Url: \"" + url + "\" already exists";
        }
        
        if( request.getParameter("add")!=null){
            //if add button is pressed return the same page
            processRequest(request, response);
        }else if (request.getParameter("view")!=null) {
            //if view button is pressed redirect to the Board table
            response.sendRedirect("BoardTable");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Board Entity";
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
