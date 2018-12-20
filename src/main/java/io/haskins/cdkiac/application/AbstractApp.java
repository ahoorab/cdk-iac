package io.haskins.cdkiac.application;

import io.haskins.cdkiac.core.AppProps;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

abstract class AbstractApp {

    final AppProps appProps = new AppProps();

    void populateAppProps(String[] args) throws IOException {

        if (args.length == 1) {
            loadDtapProperties(args[0]);
        } else if (args.length == 2) {
            loadDtapProperties(args[0]);
            loadPlatformProperties(String.format("%s-%s", args[0], args[1]));
        }
    }

    String getUniqueId() {
        return appProps.getUniqueId();
    }

    private void loadDtapProperties(String dtap) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dtap/" + dtap + ".json").getFile());
        String data = FileUtils.readFileToString(file, "UTF-8");
        addProperties(data);
    }

    private void loadPlatformProperties(String platform) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("platform/" + platform + ".json").getFile());
        String data = FileUtils.readFileToString(file, "UTF-8");
        addProperties(data);
    }

    private void addProperties(String file) {

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> myMap = gson.fromJson(file, type);

        myMap.forEach(appProps::addProp);
    }
}
