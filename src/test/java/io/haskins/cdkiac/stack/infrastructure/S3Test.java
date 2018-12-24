package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.BaseTest;
import io.haskins.cdkiac.stack.StackException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * this test is disabled at the moment while work out the best way of testing a stack
 */
public class S3Test extends BaseTest {

//    @Before
    public void setup() {
        super.setup();

        appProps.clear();

        appProps.addProp("dtap","dtap");
        appProps.addProp("vpc", "vpc");
        appProps.addProp("app_id","s3");
    }

//    @Test
    public void createTemplate() {

        S3 s3 = null;
        try {
            s3 = new S3(null, "s3-stack", appProps);
        } catch (StackException e) {
            Assert.fail("Failed to create stack");
        }
        System.out.println(createYaml(s3.toCloudFormation()));
    }
}