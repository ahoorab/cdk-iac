package io.haskins.cdkiac.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility model class for holding dtap/vpc/template specific configuration data
 */
public class AppProps {

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
    public String getPropAsString(String key) throws MissingPropertyException {
        return getPropertyByKey(key);
    }

    /**
     *
     * @param key key of the value
     * @return value as an Integer
     * @exception NumberFormatException will be thrown if the value can not be parsed as an Int
     */
    public Integer getPropAsInteger(String key) throws MissingPropertyException {
        return Integer.parseInt(getPropertyByKey(key));
    }

    /**
     *
     * @param key key of the value
     * @return value as a Boolean
     */
    public Boolean getPropAsBoolean(String key) throws MissingPropertyException {
        return Boolean.parseBoolean(getPropertyByKey(key));
    }

    /**
     *
     * @param key key of the value
     * @return returns a List of Strings. This is done by Splitting the string around any commas
     */
    public List<String> getPropAsStringList(String key) throws MissingPropertyException {
        String value = getPropertyByKey(key);
        return Lists.newArrayList(Splitter.on(",").split(value));
    }

    /**
     *
     * @param key key of the value
     * @return returns a List of Objects. This is done by Splitting the string around any commas
     */
    public List<Object> getPropAsObjectList(String key) throws MissingPropertyException {
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
            throw new MissingPropertyException("System Property -Ddtap not found");
        }

        if (props.containsKey("vpc")) {
            id.append(getPropAsString("vpc")).append("-");
        }

        if (props.containsKey("app_id")) {
            id.append(getPropAsString("app_id"));
        } else {
            throw new MissingPropertyException("System Property -Dapplication not found");
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
            throw new MissingPropertyException(String.format("Property %s not found", key));
        }

        return this.props.get(key);
    }
}
