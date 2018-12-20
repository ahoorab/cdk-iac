package io.haskins.cdkiac.application;

import software.amazon.awscdk.App;

import java.io.IOException;

public class InfluxDb extends AbstractApp  {

    private InfluxDb(String[] args) throws IOException {

        populateAppProps(args);
        setAppProperties();

        App app = new App();

        new io.haskins.cdkiac.stack.application.InfluxDb(app, "influx-db", appProps);

        app.run();
    }

    private void setAppProperties() {
        appProps.addProp("app_id", "influxdb");
        appProps.addProp("instance_type", "t2.small");
    }

    public static void main(final String[] args) throws IOException {
        new InfluxDb(args);
    }
}

