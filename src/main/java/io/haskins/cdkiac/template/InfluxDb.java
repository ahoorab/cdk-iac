package io.haskins.cdkiac.template;

import software.amazon.awscdk.App;

/**
 * Creates and InfluxDb Stack
 */
public class InfluxDb  extends CdkIacTemplate {

    private InfluxDb() {
        super();
    }

    @Override
    void defineStacks(App app) {
        new io.haskins.cdkiac.stack.application.InfluxDb(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {
        new InfluxDb();
    }
}
