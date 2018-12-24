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
import java.nio.charset.Charset;
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
    private static final String DRY_RUN = "dryrun";

    private static final String RESOURCE_FILE_PATTERN = "%s/%s.json";

    /**
     * Implementation of this method would provide the Stack Class that make up the application
     * @param app CDK App
     */
    abstract void defineStacks(App app);

    final AppProps appProps = new AppProps();
    private boolean dryRun = false;

    /**
     * Default constructor
     */
    CdkIacTemplate() {

        try {
            populateAppProps();

            App app = new App();
            defineStacks(app);
            if (!dryRun) {
                app.run();
            }
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
            loadProperties(String.format(RESOURCE_FILE_PATTERN, DTAP, System.getProperty(DTAP)));
            appProps.addProp("dtap", System.getProperty(DTAP));
        }

        if (System.getProperty(VPC) != null && System.getProperty(VPC).length() > 0) {
            loadProperties(String.format(RESOURCE_FILE_PATTERN, VPC, System.getProperty(VPC)));
            appProps.addProp("vpc", System.getProperty(VPC));
        }

        if (System.getProperty(DRY_RUN) != null && System.getProperty(DRY_RUN).length() > 0) {
            dryRun = true;
        }

        loadProperties(String.format(RESOURCE_FILE_PATTERN, APPLICATION, System.getProperty(APPLICATION)));
    }

    private void loadProperties(String property) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(property)).getFile());
        String data = FileUtils.readFileToString(file, Charset.forName("utf-8"));
        addProperties(data);
    }

    private void addProperties(String file) {

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> myMap = gson.fromJson(file, type);

        myMap.forEach(appProps::addProp);
    }
}
