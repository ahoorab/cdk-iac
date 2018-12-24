package io.haskins.cdkiac.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.haskins.cdkiac.exception.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility model class for holding dtap/vpc/template specific configuration data
 */
public class AppProps {

    private static final Logger logger = LoggerFactory.getLogger(AppProps.class);

    private final Map<String, String> props = new HashMap<>();

    /**
     * Add a property
     * @param key key for the property
     * @param value value for the property
     */
    public void addProp(String key, String value) {
        this.props.put(key, value);
    }

    /**
     * Get a property value as a String
     * @param key key of the value
     * @return value as a string
     */
    public String getPropAsString(String key) {
        return getPropertyByKey(key);
    }

    /**
     *
     * @param key key of the value
     * @return value as an Integer
     * @exception NumberFormatException will be thrown if the value can not be parsed as an Int
     */
    public Integer getPropAsInteger(String key) {
        return Integer.parseInt(getPropertyByKey(key));
    }

    /**
     *
     * @param key key of the value
     * @return value as a Boolean
     */
    public Boolean getPropAsBoolean(String key) {
        return Boolean.parseBoolean(getPropertyByKey(key));
    }

    /**
     *
     * @param key key of the value
     * @return returns a List of Strings. This is done by Splitting the string around any commas
     */
    public List<String> getPropAsStringList(String key) {
        String value = getPropertyByKey(key);
        return Lists.newArrayList(Splitter.on(",").split(value));
    }

    /**
     *
     * @param key key of the value
     * @return returns a List of Objects. This is done by Splitting the string around any commas
     */
    public List<Object> getPropAsObjectList(String key) {
        String value = getPropertyByKey(key);
        return Lists.newArrayList(Splitter.on(",").split(value));
    }

    /**
     * <p>Generates a unique ID based on the DTAP, Platform if provided, and Application id.</p>
     * @return  <p>Examples are: dtap-vpc-app_id  or   dtap-app_id</p>
     */
    public String getUniqueId() throws MissingPropertyException {

        StringBuilder id = new StringBuilder();

        if (props.containsKey("dtap")) {
            id.append(getPropAsString("dtap")).append("-");
        } else {
            logger.error("Missing dtap !");
            System.exit(1);
        }

        if (props.containsKey("vpc")) {
            id.append(getPropAsString("vpc")).append("-");
        }

        if (props.containsKey("app_id")) {
            id.append(getPropAsString("app_id"));
        } else {
            logger.error("Missing app_id !");
            System.exit(1);
        }

        return id.toString();
    }

    /**
     * Removes all keys. Really only useful for testing
     */
    public void clear() {
        this.props.clear();
    }

    /**
     * returns the number of properties
     * @return int value
     */
    public int size() { return this.props.size(); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// private methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getPropertyByKey(String key) throws MissingPropertyException  {

        if (!props.containsKey(key)) {
            logger.error(String.format("Property %s not found", key));
            throw new MissingPropertyException();
        }

        return this.props.get(key);
    }
}
