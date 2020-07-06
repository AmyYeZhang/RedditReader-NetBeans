package view;

import common.FileUtility;
import common.ValidationException;
import entity.Board;
import entity.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BoardLogic;
import logic.ImageLogic;
import logic.LogicFactory;
import reddit.Post;
import reddit.Reddit;
import reddit.Sort;

/**
 * The class ImageView is to download and save images to local directory and database, and output the images on website based on the css style.
 * 
 * @author Ye Zhang   040958453
 * @author Ziyue Wang 040919399
 */
@WebServlet(name = "ImageView", urlPatterns = {"/ImageView"})
public class ImageView extends HttpServlet {

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
            // output the image page
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            
            //use the ImageView.css style for image's output format
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/ImageView.css\">");
            out.println("<title>ImageView</title>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<div align=\"center\">");
            out.println("<div align=\"center\" class=\"imageContainer\">");
            
            //use ImageLogic to get all of the image entities, set the src for each image for output.
            ImageLogic iLogic = LogicFactory.getFor("Image");
            List<Image> entities = iLogic.getAll();
            for (Image e : entities) {
                out.printf("<img class=\"imageThumb\" src=\"ImageDelivery/%s\"/>", FileUtility.getFileName(e.getUrl()));
            }
            
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        
        //set the local directory to store the download images
        String path = System.getProperty("user.home") + "/My Documents/Reddit Images/";
        //create local directory
        FileUtility.createDirectory(path);
        
        ImageLogic iLogic = LogicFactory.getFor("Image");
        BoardLogic bLogic = LogicFactory.getFor("Board");
 
        //create a new scraper
        Reddit scrap = new Reddit();
        
        //download and save images based on board name
        for(Board b : bLogic.getAll()){
            //authenticate and set up a page for each board, subreddit with 3 posts soreted by BEST order
            scrap.authenticate().buildRedditPagesConfig(b.getName(), 3, Sort.BEST);
            
            //create a lambda that download image to local path, and save to database
            Consumer<Post> saveImage = (Post post) -> {
                //if post is an image and SFW
                if (post.isImage() && !post.isOver18()) {
                    //get the path for the image which is unique
                    String url = post.getUrl();
                    //save image in local directory which is set above
                    FileUtility.downloadAndSaveFile(url, path);

                    //check if the image url is unique
                    if(iLogic.getImageWithUrl(url)==null){
                        try{
                            //set the parameters for Image entity
                            Map<String, String[]> parameterMap = new Hashtable<>();
                            parameterMap.put(ImageLogic.TITLE, new String[]{post.getTitle()});
                            parameterMap.put(ImageLogic.URL, new String[]{post.getUrl()});
                            parameterMap.put(ImageLogic.BOARD_ID, new String[]{b.getId().toString()});
                            parameterMap.put(ImageLogic.LOCAL_PATH, new String[]{path + FileUtility.getFileName(post.getUrl())});
                            parameterMap.put(ImageLogic.DATE, new String[]{post.getDate().toString()});
                            
                            //create entity for image, and add its information to image table
                            iLogic.add(iLogic.createEntity(parameterMap));
                        
                        }catch(ValidationException ex){
                            errorMessage = ex.getMessage();
                        }
                    }else{
                        //if the url of image is duplicated, then print the error message
                        errorMessage = "Url: \"" + url + "\" already exists";
                    }
                }
            };   
        
            //get the next page 3 times and save the images.
            scrap.requestNextPage().proccessNextPage(saveImage);
        }

        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample of Images get from Reddit";
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
