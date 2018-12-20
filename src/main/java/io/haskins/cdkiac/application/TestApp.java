package io.haskins.cdkiac.application;

import io.haskins.cdkiac.stack.application.BeanstalkApiGateway;
import software.amazon.awscdk.App;

import java.io.IOException;

/**
 * App that uses the BeanstalkApiGateway stack.
 */
public class TestApp extends AbstractApp {

    private TestApp(String[] args) throws IOException {

        populateAppProps(args);
        setAppProperties();

        App app = new App();

        new BeanstalkApiGateway(app, "test-application", appProps);

        app.run();
    }

    private void setAppProperties() {
        appProps.addProp("app_id", "test-application");
        appProps.addProp("instance_type", "t2.small");
    }

    public static void main(final String[] args) throws IOException {
        new TestApp(args);
    }
}
