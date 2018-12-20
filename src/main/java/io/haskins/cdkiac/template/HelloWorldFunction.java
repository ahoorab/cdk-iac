package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.application.LambdaApiGateway;
import software.amazon.awscdk.App;

/**
 * App that uses the LambdaApiGateway Stack
 */
public class HelloWorldFunction  extends CdkIacApp {

    private HelloWorldFunction(String[] args) {
        super(args);
    }

    void setAppProperties() {
        appProps.addProp("runtime", "python3.6");
        appProps.addProp("memory_size", "128");
        appProps.addProp("handler", "lambda_function.lambda_handler");
        appProps.addProp("code_bucket", "my-bucket");
        appProps.addProp("code_key", "hello-world-function.zip");
    }

    void defineStacks(App app) {
        new LambdaApiGateway(app, getUniqueId(), appProps);
    }

    public static void main(final String[] args) {
        new HelloWorldFunction(args);
    }
}
