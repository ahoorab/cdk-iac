package io.haskins.cdkiac.app;

import io.haskins.cdkiac.stack.BeanstalkApiGateway;
import software.amazon.awscdk.App;

import java.io.IOException;

public class TestApp extends AbstractApp {

    private TestApp(String[] args) throws IOException {

        populateAppProps(args);
        setAppProperties();

        App app = new App();

        new BeanstalkApiGateway(app, "test-app", appProps);

        app.run();
    }

    private void setAppProperties() {
        appProps.addProp("app_id", "test-app");
        appProps.addProp("instance_type", "t2.small");
    }

    public static void main(final String[] args) throws IOException {
        new TestApp(args);
    }
}
