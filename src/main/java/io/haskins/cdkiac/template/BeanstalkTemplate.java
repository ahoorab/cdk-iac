package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.stack.application.BeanstalkApiGateway;
import software.amazon.awscdk.App;

/**
 * App that uses the BeanstalkApiGateway stack.
 */
public class BeanstalkTemplate extends CdkIacTemplate {

    private BeanstalkTemplate() throws TemplateException {
        super();
    }

    @Override
    protected void defineStacks(App app) throws MissingPropertyException, StackException {
        new BeanstalkApiGateway(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new BeanstalkTemplate();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
