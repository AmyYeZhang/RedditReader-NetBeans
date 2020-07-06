package logic;

import common.ValidationException;
import dal.HostDAL;
import entity.Host;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;


/**
 * The Class HostLogic is the logic for Host.
 * @author Ziyue Wang 040919399
 * @author Ye Zhang 040958453
 */
public class HostLogic extends GenericLogic<Host, HostDAL>{
    
    /** The Constant ID. */
    public static final String ID = "id";
    
    /** The Constant NAME. */
    public static final String NAME = "name";
    
    /** The Constant URL. */
    public static final String URL = "url";
    
    /** The Constant EXTRACTION_TYPE. */
    public static final String EXTRACTION_TYPE = "extractionType";
        
    /**
     * Instantiates a new Host logic.
     */
    HostLogic(){
        super(new HostDAL());
    }
    
    /**
     * Gets all Host.
     *
     * @return a list of Host
     */
    @Override
    public List<Host> getAll(){
        return get(() -> dal().findAll());
    }
    
    /**
     * Gets the Host with id.
     * This method is used for finding a Host  by id.
     *
     * @param id the id
     * @return a Host with a specific id
     */
    @Override
    public Host getWithId(int id){
        return get(() -> dal().findById(id));
    }
    
    /**
     * Gets the Host with name.
     * This method is used for finding a Host  by name.
     *
     * @param name the name
     * @return a Host with a specific name
     */
    public Host getHostWithName(String name){
        return get(()-> dal().findByName(name));
    }
    
    /**
     * Gets the Host with url.
     * This method is used for finding a Host by url.
     *
     * @param url the url
     * @return a Host with a specific url
     */
    public Host getHostWithUrl(String url){
        return get(()->dal().findByUrl(url));
    }
    
    /**
     * Gets a list of Host with extraction type.
     * This method is used for finding Host by extraction type.
     *
     * @param type the type
     * @return  a list of Host with a specific extraction type
     */
    public List<Host> getHostWithExtractionType(String type){
        return get(()->dal().findByExtractionType(type));
    }
    
    /**
     * This method is used for creating a Host entity.
     * it sets the value of entity's fields through the paramatermap.
     * It uses the Validator to determine whether the length of the filled field qualifies.
     * almost always the value is at index zero unless you have used duplicated key/name somewhere.
     *
     * @param parameterMap the parameter map
     * @return a Host object
     */
    @Override
    public Host createEntity(Map<String, String[]> parameterMap){
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        
        //create a new Host entity
        Host entity = new Host();
        
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
        String type = parameterMap.get(EXTRACTION_TYPE)[0];
        
        //validate the data
        validator.accept(name, 100);
        validator.accept(url, 255);
        validator.accept(type, 45);
        
        //set value on entity
        entity.setName(name);
        entity.setUrl(url);
        entity.setExtractionType(type);
        
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
    public List<String> getColumnNames(){
        return Arrays.asList("ID", "Name", "URL", "ExtractionType");
    }
    
    /**
     * Gets the column codes.
     *
     * @return the column codes
     */
    @Override
    public List<String> getColumnCodes(){
        return Arrays.asList(ID, NAME, URL, EXTRACTION_TYPE);
    }
    
    /**
     * Extract data as list.
     *
     * @param e the e
     * @return the list
     */
    @Override
    public List<?> extractDataAsList(Host e){
        return Arrays.asList(e.getId(), e.getName(), e.getUrl(), e.getExtractionType());
    }

}
