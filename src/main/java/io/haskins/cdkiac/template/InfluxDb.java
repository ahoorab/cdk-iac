package io.haskins.cdkiac.template;

import software.amazon.awscdk.App;

/**
 * Creates and InfluxDb Stack
 */
public class InfluxDb  extends CdkIacApp {

    private InfluxDb(String[] args) {
        super(args);
    }

    void defineStacks(App app) {
        new io.haskins.cdkiac.stack.application.InfluxDb(app, getUniqueId(), appProps);
    }

    void setAppProperties() {
        appProps.addProp("instance_type", "t2.small");
    }

    public static void main(final String[] args) {
        new InfluxDb(args);
    }
}
