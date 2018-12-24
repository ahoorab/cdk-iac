package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.stack.infrastructure.RDS;
import software.amazon.awscdk.App;

/**
 * Template that creates and RDS
 */
public class MyRds extends CdkIacTemplate {

    private MyRds() throws TemplateException {
        super();
    }

    @Override
    void defineStacks(App app) throws MissingPropertyException, StackException {
        new RDS(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new MyRds();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
