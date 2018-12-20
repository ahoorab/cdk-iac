package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.application.BeanstalkApiGateway;
import software.amazon.awscdk.App;

/**
 * App that uses the BeanstalkApiGateway stack.
 */
public class BeanstalkTemplate extends CdkIacApp {

    private BeanstalkTemplate(String[] args) {
        super(args);
    }

    void defineStacks(App app) {
        new BeanstalkApiGateway(app, getUniqueId(), appProps);
    }

    void setAppProperties() {
        appProps.addProp("instance_type", "t2.small");
    }

    public static void main(final String[] args) {
        new BeanstalkTemplate(args);
    }
}
