
package dal;
import entity.Board;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The Class BoardDAL is the data access layer for Board.
 *
 * @author Ye Zhang   040958453
 * @author Ziyue Wang 040919399
 */
public class BoardDAL extends GenericDAL<Board>{
    
    /**
     * Instantiates a new Board DAL.
     */
    public BoardDAL(){
        super(Board.class);
    }

    /**
     * Find all Board.
     *
     * @return a list of Board
     */
    @Override
    public List<Board> findAll() {
        return findResults("Board.findAll", null);
    }

    /**
     * Find Board by id.
     *
     * @param id 
     * @return a Board
     */
    @Override
    public Board findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult( "Board.findById", map);
    }
    
    /**
     * Find a list of Board by hostid.
     *
     * @param hostId 
     * @return a list of Board
     */
    public List<Board> findByHostid(int hostId){
        Map<String, Object> map = new HashMap<>();
        map.put("hostId", hostId);
        return findResults( "Board.findByHostId", map);
    }
    
    /**
     * Find a Board by url.
     *
     * @param url Board url
     * @return a Board
     */
    public Board findByUrl(String url){
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);
        return findResult( "Board.findByUrl", map);
    }
    
    /**
     * Find a list of Board by name.
     *
     * @param name Board name
     * @return a list of Board
     */
    public List<Board> findByName(String name){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return findResults( "Board.findByName", map);
    }
    
    
}
