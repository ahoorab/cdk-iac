package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.stack.application.LambdaApiGateway;
import software.amazon.awscdk.App;

/**
 * App that uses the LambdaApiGateway Stack
 */
public class HelloWorldFunction extends CdkIacTemplate {

    private HelloWorldFunction() throws TemplateException {
        super();
    }

    @Override
    protected void defineStacks(App app) throws MissingPropertyException, StackException {
        new LambdaApiGateway(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new HelloWorldFunction();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
