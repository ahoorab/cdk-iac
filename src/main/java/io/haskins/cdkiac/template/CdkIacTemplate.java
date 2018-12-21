package io.haskins.cdkiac.template;

import io.haskins.cdkiac.core.AppProps;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awscdk.App;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract class that all Template classes should extend.
 */
abstract class CdkIacTemplate {

    private static Logger logger = LoggerFactory.getLogger(CdkIacTemplate.class);

    private static final String DTAP = "dtap";
    private static final String PLATFORM = "platform";
    private static final String APPLICATION = "application";

    /**
     * Implementation of this method would provide the Stack Class that make up the application
     * @param app
     */
    abstract void defineStacks(App app, AppProps appProps);

    /**
     * Implement this method if you require additional properties that fall outside of a DTAP and Platform.
     */
    abstract void setAppProperties(AppProps appProps);

    /**
     * Default constructor
     */
    CdkIacTemplate() {

        AppProps appProps = new AppProps();

        try {
            populateAppProps(appProps);
            setAppProperties(appProps);

            App app = new App();
            defineStacks(app, appProps);
            app.run();
        } catch(IOException ioe) {
            logger.error(ioe.getMessage());
            System.exit(1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// private methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void populateAppProps(AppProps appProps) throws IOException {

        appProps.addProp("app_id", System.getProperty(APPLICATION));

        if (System.getProperty(DTAP) != null && System.getProperty(DTAP).length() > 0) {
            loadProperties(appProps, String.format("%s/%s.json", DTAP, System.getProperty(DTAP)));
        }

        if (System.getProperty(PLATFORM) != null && System.getProperty(PLATFORM).length() > 0) {
            loadProperties(appProps,String.format("%s/%s-%s.json", PLATFORM, System.getProperty(DTAP), System.getProperty(PLATFORM)));
        }

        loadProperties(appProps,String.format("%s/%s.json", APPLICATION, System.getProperty(APPLICATION)));
    }

    private void loadProperties(AppProps appProps, String property) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(property)).getFile());
        String data = FileUtils.readFileToString(file, "UTF-8");
        addProperties(appProps, data);
    }

    private void addProperties(AppProps appProps, String file) {

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> myMap = gson.fromJson(file, type);

        myMap.forEach(appProps::addProp);
    }
}
