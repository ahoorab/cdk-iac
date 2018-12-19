package io.haskins.cdkiac.core;

import java.util.HashMap;
import java.util.Map;

public class AppProps {

    private final Map<String, String> props = new HashMap<>();

    public void addProp(String key, String value) {
        this.props.put(key, value);
    }

    public String getProp(String key) {
        return this.props.get(key);
    }
}
