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
    void setAppProperties() {
        appProps.addProp("runtime", "python3.6");
        appProps.addProp("memory_size", "128");
        appProps.addProp("handler", "lambda_function.lambda_handler");
        appProps.addProp("code_bucket", "my-bucket");
        appProps.addProp("code_key", "hello-world-function.zip");
    }

    @Override
    void defineStacks(App app) {
        new LambdaApiGateway(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {
        new HelloWorldFunction();
    }
}
