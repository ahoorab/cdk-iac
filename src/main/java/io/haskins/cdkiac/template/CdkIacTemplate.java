package io.haskins.cdkiac.template;

import io.haskins.cdkiac.utils.AppProps;

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

    private static final Logger logger = LoggerFactory.getLogger(CdkIacTemplate.class);

    private static final String DTAP = "dtap";
    private static final String VPC = "vpc";
    private static final String APPLICATION = "application";

    /**
     * Implementation of this method would provide the Stack Class that make up the application
     * @param app CDK App
     */
    abstract void defineStacks(App app);

    /**
     * Implement this method if you require additional properties that fall outside of a DTAP and Platform.
     */
    abstract void setAppProperties();

    final AppProps appProps = new AppProps();

    /**
     * Default constructor
     */
    CdkIacTemplate() {

        try {
            populateAppProps();
            setAppProperties();

            App app = new App();
            defineStacks(app);
            app.run();
        } catch(IOException ioe) {
            logger.error(ioe.getMessage());
            System.exit(1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// private methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void populateAppProps() throws IOException {

        appProps.addProp("app_id", System.getProperty(APPLICATION));

        if (System.getProperty(DTAP) != null && System.getProperty(DTAP).length() > 0) {
            loadProperties(String.format("%s/%s.json", DTAP, System.getProperty(DTAP)));
        }

        if (System.getProperty(VPC) != null && System.getProperty(VPC).length() > 0) {
            loadProperties(String.format("%s/%s-%s.json", VPC, System.getProperty(DTAP), System.getProperty(VPC)));
        }

        loadProperties(String.format("%s/%s.json", APPLICATION, System.getProperty(APPLICATION)));
    }

    private void loadProperties(String property) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(property)).getFile());
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
