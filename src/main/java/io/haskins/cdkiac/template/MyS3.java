package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.stack.infrastructure.S3;
import software.amazon.awscdk.App;

/**
 * Template that creates two S3 buckets
 */
public class MyS3 extends CdkIacTemplate {

    private MyS3() throws TemplateException {
        super();
    }

    @Override
    protected void defineStacks(App app) throws MissingPropertyException, StackException {
        new S3(app, appProps.getUniqueId(), appProps);
    }

    public static void main(final String[] args) {

        try {
            new MyS3();
        } catch (TemplateException e) {
            System.out.println(e.getMessage());
        }
    }
}
