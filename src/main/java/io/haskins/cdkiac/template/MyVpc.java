package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.stack.infrastructure.VpcWithPeering;
import software.amazon.awscdk.App;

/**
 * Template that create and configures a VPC
 */
public class MyVpc extends CdkIacTemplate {

    private MyVpc() throws TemplateException {
        super();
    }

    @Override
    void defineStacks(App app) throws MissingPropertyException, StackException {
        new VpcWithPeering(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new MyVpc();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
