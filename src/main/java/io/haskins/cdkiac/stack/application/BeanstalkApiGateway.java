package io.haskins.cdkiac.stack.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.utils.IamPolicyGenerator;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplication;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplicationProps;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironment;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironmentProps;
import software.amazon.awscdk.services.iam.*;

import com.google.common.collect.ImmutableMap;
import software.amazon.awscdk.services.kms.EncryptionKey;
import software.amazon.awscdk.services.kms.EncryptionKeyRefProps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Stack that provisions an Elastic Beanstalk template that sits behind API Gateway
 */
public class BeanstalkApiGateway extends CdkIacStack {

    public BeanstalkApiGateway(final App parent,
                               final String name,
                               final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private BeanstalkApiGateway(final App parent,
                                final String name,
                                final StackProps props,
                                final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        try {

            /*
             * at some point turn these in constructs
             */
            CfnRole.PolicyProperty appPolicy =  CfnRole.PolicyProperty.builder()
                    .withPolicyName("DynamoDb")
                    .withPolicyDocument(getAppPolicyDocument())
                    .build();

            CfnRole appRole = new CfnRole(this, "ApplicationRole", CfnRoleProps.builder()
                    .withRoleName(uniqueId)
                    .withPath("/")
                    .withAssumeRolePolicyDocument(IamPolicyGenerator.getServiceTrustPolicy("ec2.amazonaws.com"))
                    .withPolicies(Collections.singletonList(appPolicy))
                    .build());

            new CfnInstanceProfile(this, "ApplicationInstanceProfile", CfnInstanceProfileProps.builder()
                    .withInstanceProfileName(uniqueId)
                    .withPath("/")
                    .withRoles(Collections.singletonList(appRole.getRoleName()))
                    .build());

            CfnApplication cfnApplication = new CfnApplication(this, "BeanstalkApplication", CfnApplicationProps.builder()
                    .withApplicationName(uniqueId)
                    .build());

            new CfnEnvironment(this, "BeanstalkEnvironment", CfnEnvironmentProps.builder()
                    .withEnvironmentName(uniqueId)
                    .withSolutionStackName(appProps.getPropAsString("solution_stack"))
                    .withCnamePrefix(uniqueId)
                    .withApplicationName(cfnApplication.getApplicationName())
                    .withOptionSettings(Arrays.asList(
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:asg").withOptionName("Availability Zones").withValue("Any 3").build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:environment").withOptionName("ServiceRole").withValue("aws-elasticbeanstalk-service-role").build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:template:environment").withOptionName("SERVER_PORT").withValue("5000").build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:healthreporting:system").withOptionName("SystemType").withValue("enhanced").build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("InstanceType").withValue(appProps.getPropAsString("instance_type")).build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("EC2KeyName").withValue(appProps.getPropAsString("keypair")).build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("SSHSourceRestriction").withValue(String.format("tcp,22,22,%s", appProps.getPropAsString("bastion_sg"))).build(),
                            CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:ec2:vpc").withOptionName("VPCId").withValue(appProps.getPropAsString("vpc_id")).build()
                    ))
                    .build());

            CfnRestApi restApi = new CfnRestApi(this, "RestApi", CfnRestApiProps.builder()
                    .withName(uniqueId)
                    .build());

            CfnResource cfnResource = new CfnResource(this, "CfnRestApi", CfnResourceProps.builder()
                    .withPathPart("{proxy+}")
                    .withRestApiId(restApi.getRestApiId())
                    .withParentId(restApi.getRestApiRootResourceId())
                    .build());

            new CfnMethod(this, "RestApiMethod", CfnMethodProps.builder()
                    .withRestApiId(restApi.getRestApiId())
                    .withResourceId(cfnResource.getResourceId())
                    .withHttpMethod("ANY")
                    .withAuthorizationType("NONE")
                    .withRequestParameters(ImmutableMap.of("method.request.path.proxy", true))
                    .withIntegration(CfnMethod.IntegrationProperty.builder()
                            .withIntegrationHttpMethod("ANY")
                            .withType("HTTP_PROXY")
                            .withUri("http://" + uniqueId + ".eu-west-1.elasticbeanstalk.com/{proxy}")
                            .build())
                    .build());

        } catch (MissingPropertyException e) {
            throw new StackException(e.getMessage());
        }

    }

    private ObjectNode getAppPolicyDocument() {

        List<JsonNode> statements = new ArrayList<>();

        statements.add(IamPolicyGenerator.getPolicyStatement("Allow", Collections.singletonList("dynamodb:*"), Collections.singletonList("*")));

        return IamPolicyGenerator.getPolicyDocument(statements);
    }
}
