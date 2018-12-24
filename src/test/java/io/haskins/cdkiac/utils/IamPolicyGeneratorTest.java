package io.haskins.cdkiac.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class IamPolicyGeneratorTest {

    @Test
    public void getServiceTrustPolicy() {

        String expected = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":[\"ec2.amazonaws.com\"]},\"Action\":\"sts:AssumeRole\"}]}";

        ObjectNode policy = IamPolicyGenerator.getServiceTrustPolicy("ec2.amazonaws.com");
        String jsonPolicy = policy.toString();

        Assert.assertEquals(expected, jsonPolicy);
    }

    @Test
    public void getAwsTrustPolicy() {

        String expected = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"arn:aws:iam::0123456789:user/mark.haskins\"]},\"Action\":\"sts:AssumeRole\"}]}";

        ObjectNode policy = IamPolicyGenerator.getAwsTrustPolicy("arn:aws:iam::0123456789:user/mark.haskins");
        String jsonPolicy = policy.toString();

        Assert.assertEquals(expected, jsonPolicy);
    }

    @Test
    public void getPolicyStatement() {

        String expected = "{\"Effect\":\"Allow\",\"Action\":[\"s3:List\",\"s3:Get\"],\"Resource\":[\"*\"]}";

        JsonNode statement = IamPolicyGenerator.getPolicyStatement("Allow", Arrays.asList("s3:List", "s3:Get"), Collections.singletonList("*"));
        String jsonPolicy = statement.toString();

        Assert.assertEquals(expected, jsonPolicy);
    }

    @Test
    public void getPolicyDocument() {

        String expected = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"s3:List\",\"s3:Get\"],\"Resource\":[\"*\"]}]}";

        JsonNode statement = IamPolicyGenerator.getPolicyStatement("Allow", Arrays.asList("s3:List", "s3:Get"), Collections.singletonList("*"));
        ObjectNode policy = IamPolicyGenerator.getPolicyDocument(Collections.singletonList(statement));

        String jsonPolicy = policy.toString();

        Assert.assertEquals(expected, jsonPolicy);
    }
}