package io.haskins.cdkiac.template;

import io.haskins.cdkiac.core.AppProps;
import io.haskins.cdkiac.stack.infrastructure.S3;
import software.amazon.awscdk.App;

import java.util.Map;

/**
 * Template that creates two S3 buckets
 */
public class MyS3 extends CdkIacTemplate {

    private MyS3() {
        super();
    }

    @Override
    void defineStacks(App app, AppProps appProps) {
        new S3(app, appProps.getUniqueId(), appProps);
    }

    @Override
    void setAppProperties(AppProps appProps) {
    }

    public static void main(final String[] args) {
        new MyS3();
    }
}
