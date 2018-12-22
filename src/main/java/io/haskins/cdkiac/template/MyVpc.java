package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.infrastructure.VpcWithPeering;
import software.amazon.awscdk.App;

/**
 * Template that create and configures a VPC
 */
public class MyVpc extends CdkIacTemplate {

    private MyVpc() {
        super();
    }

    @Override
    void defineStacks(App app) {
        new VpcWithPeering(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {
        new MyVpc();
    }
}
