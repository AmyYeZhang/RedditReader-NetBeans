package logic;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory for creating Logic objects.
 *
 * @author Ziyue Wang 040919399
 * @author Ye Zhang 040958453
 */
public abstract class LogicFactory {

    /** The Constant PACKAGE. */
    private static final String PACKAGE = "logic.";
    
    /** The Constant SUFFIX. */
    private static final String SUFFIX = "Logic";

    /**
     * Instantiates a new logic factory.
     */
    private LogicFactory() {

    }

   
    /**
     * Create the class name from View based on Table name.
     *
     * @param <T> the generic type
     * @param entityName 
     * @return return the logic class name with logic package name
     */
    public static <T> T getFor(String entityName) {

        try {
            return getFor((Class<T>) Class.forName(PACKAGE + entityName + SUFFIX));
        } catch (ClassNotFoundException ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalArgumentException(ex);
        }

    }

    /**
     * Create a new object of the class.
     * it calls getDeclaredConstructor() in Class<T> to create a new instance and return this instance.
     * @param <T> the generic type
     * @param type 
     * @return return the object of the class
     */
    public static <T> T getFor(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalArgumentException(ex);
        }

    }
}
