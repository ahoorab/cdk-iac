package io.haskins.cdkiac.stack.application;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplication;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplicationProps;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironment;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironmentProps;
import software.amazon.awscdk.services.iam.*;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collections;

/**
 * Stack that provisions an Elastic Beanstalk application that sits behind API Gateway
 */
public class BeanstalkApiGateway extends Stack {

    public BeanstalkApiGateway(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private BeanstalkApiGateway(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props);

        String uniqueId = appProps.getUniqueId();

        /*
         * at some point turn these in constructs
         */
        Role appRole = new Role(this, "ApplicationRole", RoleProps.builder()
                .withRoleName(uniqueId)
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .build());

        new Role(this, "JenkinsRole", RoleProps.builder()
                .withRoleName(uniqueId + "-jenkins")
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .build());


        new CfnInstanceProfile(this, "ApplicationInstanceProfile", CfnInstanceProfileProps.builder()
                .withInstanceProfileName(uniqueId)
                .withPath("/")
                .withRoles(Collections.singletonList(appRole.getRoleName()))
                .build());

        CfnApplication CfnApplication = new CfnApplication(this, "BeanstalkApplication", CfnApplicationProps.builder()
                .withApplicationName(uniqueId)
                .build());

        new CfnEnvironment(this, "BeanstalkEnvironment", CfnEnvironmentProps.builder()
                .withEnvironmentName(uniqueId)
                .withSolutionStackName("64bit Amazon Linux 2018.03 v2.7.7 running Java 8")
                .withCnamePrefix(uniqueId)
                .withApplicationName(CfnApplication.getApplicationName())
                .withOptionSettings(Arrays.asList(
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:asg").withOptionName("Availability Zones").withValue("Any 3").build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:environment").withOptionName("ServiceRole").withValue("aws-elasticbeanstalk-service-role").build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:application:environment").withOptionName("SERVER_PORT").withValue("5000").build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:healthreporting:system").withOptionName("SystemType").withValue("enhanced").build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("InstanceType").withValue(appProps.getPropAsString("instance_type")).build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("EC2KeyName").withValue(appProps.getPropAsString("keypair")).build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("SSHSourceRestriction").withValue(String.format("tcp,22,22,%s", appProps.getPropAsStringList("bastionSG"))).build(),
                        CfnEnvironment.OptionSettingProperty.builder().withNamespace("aws:ec2:vpc").withOptionName("VPCId").withValue(appProps.getPropAsString("vpcId")).build()
                ))
                .build());

        CfnRestApi restApi = new CfnRestApi(this, "RestApi", CfnRestApiProps.builder()
                .withName(uniqueId)
                .build());

        CfnResource CfnResource = new CfnResource(this, "CfnRestApi", CfnResourceProps.builder()
                .withPathPart("{proxy+}")
                .withRestApiId(restApi.getRestApiId())
                .withParentId(restApi.getRestApiRootResourceId())
                .build());

        new CfnMethod(this, "RestApiMethod", CfnMethodProps.builder()
                .withRestApiId(restApi.getRestApiId())
                .withResourceId(CfnResource.getResourceId())
                .withHttpMethod("ANY")
                .withAuthorizationType("NONE")
                .withRequestParameters(ImmutableMap.of("method.request.path.proxy", true))
                .withIntegration(CfnMethod.IntegrationProperty.builder()
                        .withIntegrationHttpMethod("ANY")
                        .withType("HTTP_PROXY")
                        .withUri("http://" + uniqueId + ".eu-west-1.elasticbeanstalk.com/{proxy}")
                        .build())
                .build());
    }
}
