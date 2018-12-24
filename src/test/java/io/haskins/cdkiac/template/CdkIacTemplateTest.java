package io.haskins.cdkiac.template;

import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.utils.MissingPropertyException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CdkIacTemplateTest {

    @Before
    public void setUp() {
        System.setProperty("dtap", "test_dtap");
        System.setProperty("vpc", "test_vpc");
        System.setProperty("application", "test_application");
        System.setProperty("dryrun", "true");
    }

    @Test
    public void loadProperties() {

        String expectedKeyPair = "vpc-key";
        String expectedAccountID = "1234567890";
        String expectedSubnetId = "subnet-12345678";
        String expectedInstanceId = "m5.medium";

        TestTemplate testTemplate = null;
        try {
            testTemplate = new TestTemplate();
        } catch (TemplateException e) {
            Assert.fail("Failed to instantiate an instance of TestTemplate");
        }

        if (testTemplate != null) {

            AppProps appProps = testTemplate.appProps;

            try {
                Assert.assertEquals(expectedKeyPair, appProps.getPropAsString("keypair"));
                Assert.assertEquals(expectedAccountID, appProps.getPropAsString("account_id"));
                Assert.assertEquals(expectedSubnetId, appProps.getPropAsString("subnet"));
                Assert.assertEquals(expectedInstanceId, appProps.getPropAsString("instance_type"));
            } catch (MissingPropertyException e) {
                Assert.fail(e.getMessage());
            }

        }
    }
}