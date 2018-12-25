package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import software.amazon.awscdk.App;

/**
 * Creates and InfluxDb Stack
 */
public class InfluxDb  extends CdkIacTemplate {

    private InfluxDb() throws TemplateException {
        super();
    }

    @Override
    protected void defineStacks(App app) throws MissingPropertyException, StackException {
        new io.haskins.cdkiac.stack.application.InfluxDb(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new InfluxDb();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
