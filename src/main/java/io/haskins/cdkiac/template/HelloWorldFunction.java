package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.application.LambdaApiGateway;
import software.amazon.awscdk.App;

/**
 * App that uses the LambdaApiGateway Stack
 */
public class HelloWorldFunction  extends CdkIacTemplate {

    private HelloWorldFunction() {
        super();
    }

    @Override
    void defineStacks(App app) {
        new LambdaApiGateway(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {
        new HelloWorldFunction();
    }
}
