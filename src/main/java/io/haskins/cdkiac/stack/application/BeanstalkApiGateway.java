package io.haskins.cdkiac.stack.application;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.cloudformation.*;
import software.amazon.awscdk.services.elasticbeanstalk.cloudformation.ApplicationResource;
import software.amazon.awscdk.services.elasticbeanstalk.cloudformation.ApplicationResourceProps;
import software.amazon.awscdk.services.elasticbeanstalk.cloudformation.EnvironmentResource;
import software.amazon.awscdk.services.elasticbeanstalk.cloudformation.EnvironmentResourceProps;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.iam.cloudformation.InstanceProfileResource;
import software.amazon.awscdk.services.iam.cloudformation.InstanceProfileResourceProps;

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


        new InstanceProfileResource(this, "ApplicationInstanceProfile", InstanceProfileResourceProps.builder()
                .withInstanceProfileName(uniqueId)
                .withPath("/")
                .withRoles(Collections.singletonList(appRole.getRoleName()))
                .build());

        ApplicationResource applicationResource = new ApplicationResource(this, "BeanstalkApplication", ApplicationResourceProps.builder()
                .withApplicationName(uniqueId)
                .build());

        new EnvironmentResource(this, "BeanstalkEnvironment", EnvironmentResourceProps.builder()
                .withEnvironmentName(uniqueId)
                .withSolutionStackName("64bit Amazon Linux 2018.03 v2.7.7 running Java 8")
                .withCnamePrefix(uniqueId)
                .withApplicationName(applicationResource.getApplicationName())
                .withOptionSettings(Arrays.asList(
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:autoscaling:asg").withOptionName("Availability Zones").withValue("Any 3").build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:environment").withOptionName("ServiceRole").withValue("aws-elasticbeanstalk-service-role").build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:application:environment").withOptionName("SERVER_PORT").withValue("5000").build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:elasticbeanstalk:healthreporting:system").withOptionName("SystemType").withValue("enhanced").build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("InstanceType").withValue(appProps.getPropAsString("instance_type")).build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("EC2KeyName").withValue(appProps.getPropAsString("keypair")).build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:autoscaling:launchconfiguration").withOptionName("SSHSourceRestriction").withValue(String.format("tcp,22,22,%s", appProps.getPropAsStringList("bastionSG"))).build(),
                        EnvironmentResource.OptionSettingProperty.builder().withNamespace("aws:ec2:vpc").withOptionName("VPCId").withValue(appProps.getPropAsString("vpcId")).build()
                ))
                .build());

        RestApiResource restApi = new RestApiResource(this, "RestApi", RestApiResourceProps.builder()
                .withName(uniqueId)
                .build());

        Resource resource = new Resource(this, "RestApiResource", ResourceProps.builder()
                .withPathPart("{proxy+}")
                .withRestApiId(restApi.getRestApiId())
                .withParentId(restApi.getRestApiRootResourceId())
                .build());

        new MethodResource(this, "RestApiMethod", MethodResourceProps.builder()
                .withRestApiId(restApi.getRestApiId())
                .withResourceId(resource.getResourceId())
                .withHttpMethod("ANY")
                .withAuthorizationType("NONE")
                .withRequestParameters(ImmutableMap.of("method.request.path.proxy", true))
                .withIntegration(MethodResource.IntegrationProperty.builder()
                        .withIntegrationHttpMethod("ANY")
                        .withType("HTTP_PROXY")
                        .withUri("http://" + uniqueId + ".eu-west-1.elasticbeanstalk.com/{proxy}")
                        .build())
                .build());
    }
}
