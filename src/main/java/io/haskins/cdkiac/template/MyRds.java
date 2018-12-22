package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.infrastructure.RDS;
import software.amazon.awscdk.App;

/**
 * Template that creates and RDS
 */
public class MyRds extends CdkIacTemplate {

    private MyRds() {
        super();
    }

    @Override
    void defineStacks(App app) {
        new RDS(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {
        new MyRds();
    }
}
