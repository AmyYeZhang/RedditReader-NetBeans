
package logic;

import common.ValidationException;
import dal.BoardDAL;
import entity.Board;
import entity.Host;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 * The Class BoardLogic is the logic for Board.
 *
 * @author Ye Zhang   040958453
 * @author Ziyue Wang 040919399
 */
public class BoardLogic extends GenericLogic<Board, BoardDAL>{

    /** The Constant ID. */
    public static final String ID = "id";

    /** The Constant URL. */
    public static final String URL = "url";

    /** The Constant NAME. */
    public static final String NAME = "name";

    /** The Constant HOST_ID. */
    public static final String HOST_ID = "hostId";

    /**
     * Instantiates a new Board logic.
     */
    public BoardLogic() {
        super(new BoardDAL());
    }

    /**
     * Gets all Boards.
     *
     * @return a List of Board
     */
    @Override
    public List<Board> getAll() {
        return get(() -> dal().findAll());
    }

    /**
     * Gets the Board with id.
     * This method is used for finding a Board by id.
     * @param id the id
     * @return a Board object
     */
    @Override
    public Board getWithId(int id) {
        return get(() -> dal().findById(id));
    }
    
    /**
     * Gets the boards with host ID.
     * This method is used for finding Board list by host id.
     * @param hostId the host id
     * @return a Board list
     */
    public List<Board> getBoardsWithHostID(int hostId){
        return get(() -> dal().findByHostid(hostId));
    }
    
    /**
     * Gets the Boards with name.
     * This method is used for finding Board list by name.
     * @param name the name
     * @return a Board list with specific name
     */
    public List<Board> getBoardsWithName(String name){
        return get(() -> dal().findByName(name));
    }
    
    /**
     * Gets the Board with url.
     * This method is used for finding a Board by url.
     * @param url the url
     * @return the Board object with specific url
     */
    public Board getBoardWithUrl(String url){
        return get(() -> dal().findByUrl(url));
    }
    
    /**
     * Creates the entity of Board.
     * This method is used for creating a Board entity.
     * it sets the value of entity's fields through the paramatermap.
     * It uses the Validator to determine whether the length of the filled field qualifies.
     * almost always the value is at
     * index zero unless you have used duplicated key/name somewhere.
     * @param parameterMap the parameter map
     * @return a Board
     */
    @Override
    public Board createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        
        //create a new Board entity
        Board entity = new Board();
        
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
        String name = parameterMap.get(NAME)[0];
        String url = parameterMap.get(URL)[0];
        String hostId = parameterMap.get(HOST_ID)[0];
        
        //validate the data
        validator.accept(name, 100);
        validator.accept(url, 255);
        
        //set value on entity
        entity.setName(name);
        entity.setUrl(url);
        
        //dont use DAL, use the logic - Shawn
        HostLogic hLogic = new HostLogic();
        Host host = hLogic.getWithId(Integer.parseInt(hostId));
        entity.setHostid(host);
        
        return entity;
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
        return Arrays.asList("ID", "Host_ID", "URL", "Name");
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
        return Arrays.asList(ID, HOST_ID, URL, NAME);
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
    public List<?> extractDataAsList(Board e) {
        return Arrays.asList(e.getId(), e.getHostid().getId(), e.getUrl(), e.getName());
    }

}
