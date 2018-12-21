package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.BaseTest;
import org.junit.Before;
import org.junit.Test;

public class S3Test extends BaseTest {

    @Before
    public void setup() {
        super.setup();

        appProps.clear();

        appProps.addProp("dtap","dtap");
        appProps.addProp("platform","platform");
        appProps.addProp("app_id","s3");
    }

    @Test
    public void createTemplate() {
        S3 s3 = new S3(null, "s3-stack", appProps);
        System.out.println(createYaml(s3.toCloudFormation()));
    }
}