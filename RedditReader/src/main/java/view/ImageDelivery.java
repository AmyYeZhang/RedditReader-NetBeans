package view;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

/**
 * The class ImageDelivery is to get the images from local directory and output them on the website one by one.
 * 
 * @author Ye Zhang   040958453
 * @author Ziyue Wang 040919399
 */
@WebServlet(name = "ImageDelivery", urlPatterns = {"/ImageDelivery/*"})
public class ImageDelivery extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
    }
    
    /**
     * Get the images from local directory one by one, then output the images on the website one by one.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        //processRequest(request, response);
        
        //set the path of imageDirectory to local user home directory: C:\\Users\\zhang 
        String imageDirectory = System.getProperty("user.home");
        
        //get the file name of the image, such as '\j6QCsY9.jpg'
        String fileName = request.getPathInfo();
        
        //set the path of the image, such as C:\Users\zhang\My Documents\Reddit Images\j6QCsY9.jpg
        File file = new File(imageDirectory+"/My Documents/Reddit Images", fileName);
        
        //set "Content-Type", such as 'image/jpeg'
        response.setHeader("Content-Type", getServletContext().getMimeType(fileName));
        
        //set "Content-Length", convert file.length() from long to String
        response.setHeader("Content-Length", String.valueOf(file.length()));
        
        //set "Content-Disposition", such as "inline; fileName="/cdkhofwhii851.jpg"
        response.setHeader("Content-Disposition", "inline; fileName=\"" + fileName + "\"");
        
        //Copies all bytes from a file to an output stream
        Files.copy(file.toPath(), response.getOutputStream());
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "ImageDelivery";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
