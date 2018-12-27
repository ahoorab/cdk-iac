package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.stack.infrastructure.Route53;
import io.haskins.cdkiac.utils.MissingPropertyException;
import software.amazon.awscdk.App;

public class InfTest extends CdkIacTemplate {

    private InfTest() throws TemplateException {
        super();
    }

    @Override
    protected void defineStacks(App app) throws MissingPropertyException, StackException {
        new Route53(app, appProps.getUniqueId() + "-route53", appProps);
    }

    public static void main(final String[] args) {

        try {
            new InfTest();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}