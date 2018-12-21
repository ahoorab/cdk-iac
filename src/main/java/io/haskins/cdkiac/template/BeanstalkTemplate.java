package io.haskins.cdkiac.template;

import io.haskins.cdkiac.core.AppProps;
import io.haskins.cdkiac.stack.application.BeanstalkApiGateway;
import software.amazon.awscdk.App;

/**
 * App that uses the BeanstalkApiGateway stack.
 */
public class BeanstalkTemplate extends CdkIacTemplate {

    private BeanstalkTemplate() {
        super();
    }

    @Override
    void defineStacks(App app, AppProps appProps) {
        new BeanstalkApiGateway(app, appProps.getUniqueId(), appProps);
    }

    @Override
    void setAppProperties(AppProps appProps) {
        // none applicable
    }

    public static void main(final String[] args) {
        new BeanstalkTemplate();
    }
}
