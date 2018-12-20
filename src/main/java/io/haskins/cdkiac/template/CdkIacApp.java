package io.haskins.cdkiac.template;

import io.haskins.cdkiac.core.AppProps;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import software.amazon.awscdk.App;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

abstract class CdkIacApp {

    abstract void defineStacks(App app);
    abstract void setAppProperties();

    final AppProps appProps = new AppProps();

    CdkIacApp(String[] args) {

        try {
            populateAppProps(args);
            setAppProperties();

            App app = new App();
            defineStacks(app);
            app.run();
        } catch(IOException ioe) {
            System.out.println(ioe.getMessage());
            System.exit(1);
        }
    }


    String getUniqueId() {
        return appProps.getUniqueId();
    }

    private void populateAppProps(String[] args) throws IOException {

        appProps.addProp("app_id", args[0]);

        if (args.length == 2) {
            loadDtapProperties(args[1]);
        } else if (args.length == 3) {
            loadDtapProperties(args[1]);
            loadPlatformProperties(String.format("%s-%s", args[1], args[2]));
        }
    }

    private void loadDtapProperties(String dtap) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("dtap/" + dtap + ".json")).getFile());
        String data = FileUtils.readFileToString(file, "UTF-8");
        addProperties(data);
    }

    private void loadPlatformProperties(String platform) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("platform/" + platform + ".json")).getFile());
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
