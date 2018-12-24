package io.haskins.cdkiac.template;

import io.haskins.cdkiac.utils.AppProps;
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

        TestTemplate testTemplate = new TestTemplate();
        AppProps appProps = testTemplate.appProps;

        Assert.assertEquals(expectedKeyPair, appProps.getPropAsString("keypair"));
        Assert.assertEquals(expectedAccountID, appProps.getPropAsString("account_id"));
        Assert.assertEquals(expectedSubnetId, appProps.getPropAsString("subnet"));
        Assert.assertEquals(expectedInstanceId, appProps.getPropAsString("instance_type"));

    }
}