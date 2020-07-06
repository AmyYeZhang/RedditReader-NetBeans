package logic;

import common.ValidationException;
import dal.ImageDAL;
import entity.Board;
import entity.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 * The Class ImageLogic is the logic for Image.
 *
 * @author Ziyue Wang 040919399
 * @author Ye Zhang 040958453
 */
public class ImageLogic extends GenericLogic<Image, ImageDAL>{

    /** The Constant FORMATTER. */
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
    
    /** The Constant ID. */
    public static final String ID = "id";
    
    /** The Constant URL. */
    public static final String URL = "url";
    
    /** The Constant TITLE. */
    public static final String TITLE = "title";
    
    /** The Constant DATE. */
    public static final String DATE = "date";
    
    /** The Constant LOCAL_PATH. */
    public static final String LOCAL_PATH = "localPath";
    
    /** The Constant BOARD_ID. */
    public static final String BOARD_ID = "boardId";
    
    /**
     * Instantiates a new Image logic.
     */
    public ImageLogic() {
        super( new ImageDAL());
    }

    /**
     * Gets the all Image.
     *
     * @return a Image list
     */
    @Override
    public List<Image> getAll() {
        return get(() -> dal().findAll());
    }

    /**
     * Gets the Image with specific id.
     *
     * @param id 
     * @return a Image with specific id.
     */
    @Override
    public Image getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    /**
     * Gets a list of Image with Board id.
     *
     * @param boardID 
     * @return a list of Image with a specific Board id
     */
    public List<Image> getImagesWithBoardId(int boardID) {
        return get(() -> dal().findByBoardId(boardID));
    }
    
    /**
     * Gets a list of Image with title.
     *
     * @param title 
     * @return a list of Image with specific title
     */
    public List<Image> getImagesWithTitle(String title) {
        return get(() -> dal().findByTitle(title));
    }
    
    /**
     * Gets the Image with url.
     *
     * @param url 
     * @return a Image with specific url
     */
    public Image getImageWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }
    
    /**
     * Gets the Image with local path.
     *
     * @param path 
     * @return the Image with local path
     */
    public Image getImageWithLocalPath(String path) {
        return get(() -> dal().findByLocalPath(path));
    }
    
    /**
     * Gets a list of Image with specific date.
     *
     * @param date the date
     * @return a list of Image with specific date
     */
    public List<Image> getImagesWithDate(Date date) {
        return get(() -> dal().findByDate(date));
    }
    
    /**
     * Convert date into string with a specific format
     *  
     * @param date 
     * @return a String of date with a specific format
     */
    public String convertDate(Date date) {
        return FORMATTER.format(date);
    }
    
    /**
     * This method is used for creating Image entity.
     * it sets the value of entity's fields through the paramatermap.
     * It uses the Validator to determine whether the length of the filled field qualifies.
     * Because the primary key of the Board table is the foreign key of the image table, 
     * we need to get the board object with the appropriate ID.
     * The date is converted into an appropriate form by using formatter.
     * If the conversion fails, the date of the image will be set to the current system default time
     * 
     * @param parameterMap 
     * @return a Image
     */
    @Override
    public Image createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        
        //create a new Image entity
        Image entity = new Image();
        
        //if ID is exist
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        
        //error checking
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };
        
        //extract the data from map
        String title = parameterMap.get(TITLE)[0];
        String url = parameterMap.get(URL)[0];
        String boardId = parameterMap.get(BOARD_ID)[0];
        String date = parameterMap.get(DATE)[0];
        String path = parameterMap.get(LOCAL_PATH)[0];
        
        //Board part should be finished in CreateBoard view
        BoardLogic bLogic = LogicFactory.getFor("Board");
        Board board = bLogic.getWithId(Integer.parseInt(boardId));
        
        //validate the data
        validator.accept(title, 1000);
        validator.accept(url, 255);
        validator.accept(path, 255);
        
        //set value on entity
        entity.setTitle(title);
        entity.setUrl(url);
        entity.setLocalPath(path);
        entity.setBoard(board);
        try{
            entity.setDate(FORMATTER.parse(date));
        }catch(ParseException e){
            entity.setDate(Date.from(Instant.now(Clock.systemDefaultZone())));
            
        }
        
        return entity;
    }
    
    /**
     * Update Image is not implemented.
     *
     * @param parameterMap the parameter map
     * @return the Image
     */
    public Image updateEntity(Map<String, String[]> parameterMap) {
        return createEntity(parameterMap);
    }
    
    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnCodes and extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "BoardID", "Title", "URL", "LocalPath", "Date");
    }

    /**
     * this method returns a list of column names that match the official column
     * names in the db. by having all names in one location there is less chance of mistakes.
     *
     * this list must be in the same order as getColumnNames and extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, BOARD_ID, TITLE, URL, LOCAL_PATH, DATE);
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getBoard().getId(), e.getTitle(), e.getUrl(), e.getLocalPath(), e.getDate());
    }
    
}
