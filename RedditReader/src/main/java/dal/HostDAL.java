
package dal;
import entity.Host;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class HostDAL is the data access layer for Host.
 *
 * @author Ziyue Wang 040919399
 * @author Ye Zhang   040958453
 * 
 */
public class HostDAL extends GenericDAL<Host>{

    /**
     * Instantiates a new Host DAL.
     */
    public HostDAL(){
        super(Host.class);
        
    }

    /**
     * Find all Hosts.
     *
     * @return the Host list
     */
    @Override
    public List<Host> findAll(){
        return findResults( "Host.findAll", null);
    }
    
    /**
     * Find Host by id.
     *
     * @param id 
     * @return the Host
     */
    @Override
    public Host findById(int id){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult( "Host.findById", map);
    }
    
    /**
     * Find Host by name.
     *
     * @param name 
     * @return the Host
     */
    public Host findByName(String name){
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return findResult( "Host.findByName", map);
    }
    
    /**
     * Find Host by url.
     *
     * @param url 
     * @return the Host
     */
    public Host findByUrl(String url){
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);
        return findResult( "Host.findByUrl", map);
    }
    
    /**
     * Find Host by extraction type.
     *
     * @param type 
     * @return the Host list
     */
    public List<Host> findByExtractionType(String type){
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        return findResults( "Host.findByExtractionType", map);
    }

}
