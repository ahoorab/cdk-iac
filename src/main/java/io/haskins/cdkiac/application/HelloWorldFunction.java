package io.haskins.cdkiac.application;

import io.haskins.cdkiac.stack.application.LambdaApiGateway;
import software.amazon.awscdk.App;

import java.io.IOException;

/**
 * App that uses the LambdaApiGateway Stack
 */
public class HelloWorldFunction extends AbstractApp {

    private HelloWorldFunction(String[] args) throws IOException {

        populateAppProps(args);
        setAppProperties();

        App app = new App();
        new LambdaApiGateway(app, getUniqueId(), appProps);
        app.run();
    }

    private void setAppProperties() {
        appProps.addProp("app_id", "hello-world-function");
        appProps.addProp("runtime", "python3.6");
        appProps.addProp("memory_size", "128");
        appProps.addProp("handler", "lambda_function.lambda_handler");
        appProps.addProp("code_bucket", "my-bucket");
        appProps.addProp("code_key", "hello-world-function.zip");
    }

    public static void main(final String[] args) throws IOException {
        new HelloWorldFunction(args);
    }
}