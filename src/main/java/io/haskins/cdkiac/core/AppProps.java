package io.haskins.cdkiac.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility model class for holding dtap/platform/application specific configuration data
 */
public class AppProps {

    private final Map<String, String> props = new HashMap<>();

    public void addProp(String key, String value) {
        this.props.put(key, value);
    }

    public String getPropAsString(String key) {
        return this.props.get(key);
    }

    public Integer getPropAsInteger(String key) {
        return Integer.parseInt(this.props.get(key));
    }

    public Boolean getPropAsBoolean(String key) {
        return Boolean.parseBoolean(this.props.get(key));
    }

    public List<String> getPropAsStringList(String key) {
        String value = this.props.get(key);
        return Lists.newArrayList(Splitter.on(",").split(value));
    }

    public List<Object> getPropAsObjectList(String key) {
        String value = this.props.get(key);
        return Lists.newArrayList(Splitter.on(",").split(value));
    }

    public String getUniqueId() {

        StringBuilder id = new StringBuilder();

        if (props.containsKey("dtap")) {
            id.append(getPropAsString("dtap")).append("-");
        }

        if (props.containsKey("platform")) {
            id.append(getPropAsString("platform")).append("-");
        }

        if (props.containsKey("app_id")) {
            id.append(getPropAsString("app_id"));
        }

        return id.toString();
    }
}