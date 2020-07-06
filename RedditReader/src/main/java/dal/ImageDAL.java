
package dal;
import entity.Image;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class ImageDAL is the data access layer for Image.
 *
 * @author Ye Zhang   040958453
 * @author Ziyue Wang 040919399
 */
public class ImageDAL extends GenericDAL<Image>{
    
    /**
     * Instantiates a new Image DAL.
     */
    public ImageDAL(){
        super(Image.class);
    }

    /**
     * Find all Image.
     *
     * @return the Image list
     */
    @Override
    public List<Image> findAll() {
        return findResults("Image.findAll", null);
    }

    /**
     * Find Image by id.
     *
     * @param id 
     * @return the Image
     */
    @Override
    public Image findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Image.findById", map);
    }
    
    /**
     * Find Image by Board id.
     *
     * @param BoardId 
     * @return the Image list
     */
    public List<Image> findByBoardId(int BoardId){
        Map<String, Object> map = new HashMap<>();
        map.put("BoardId", BoardId);
        return findResults( "Image.findByBoardId", map);
    }
    
    /**
     * Find Image by title.
     *
     * @param title 
     * @return the Image list
     */
    public List<Image> findByTitle(String title){
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        return findResults( "Image.findByTitle", map);
    }
    
    /**
     * Find Image by url.
     *
     * @param url 
     * @return the Image
     */
    public Image findByUrl(String url){
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);
        return findResult( "Image.findByUrl", map);
    }
    
    /**
     * Find Image by date.
     *
     * @param date
     * @return the Image list
     */
    public List<Image> findByDate(Date date){
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        return findResults( "Image.findByDate", map);
    }
    
    /**
     * Find Image by local path.
     *
     * @param localPath 
     * @return the Image
     */
    public Image findByLocalPath(String localPath){
        Map<String, Object> map = new HashMap<>();
        map.put("localPath", localPath);
        return findResult( "Image.findByLocalPath", map);
    }
}
