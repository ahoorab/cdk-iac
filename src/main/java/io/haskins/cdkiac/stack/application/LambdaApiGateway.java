package io.haskins.cdkiac.stack.application;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.CfnFunctionProps;

public class LambdaApiGateway extends Stack {

    public LambdaApiGateway(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private LambdaApiGateway(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props);

        String uniqueId = appProps.getUniqueId();

        Role appRole = new Role(this, "LambdaRole", RoleProps.builder()
                .withRoleName(uniqueId)
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                .build());

        CfnFunction lambda = new CfnFunction(this, "LambdaFunction", CfnFunctionProps.builder()
                .withFunctionName(uniqueId)
                .withRuntime(appProps.getPropAsString("runtime"))
                .withMemorySize(appProps.getPropAsInteger("memory_size"))
                .withHandler(appProps.getPropAsString("handler"))
                .withRole(appRole.getRoleName())
                .withCode(CfnFunction.CodeProperty.builder()
                        .withS3Bucket(appProps.getPropAsString("code_bucket"))
                        .withS3Key(appProps.getPropAsString("code_key"))
                        .build())
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
                .withIntegration(CfnMethod.IntegrationProperty.builder()
                        .withIntegrationHttpMethod("ANY")
                        .withType("AWS_PROXY")
                        .withUri(new StringBuilder()
                                .append("arn:aws:apigateway:eu-west-1:lambda:path/2015-03-31/functions")
                                .append(lambda.getFunctionArn())
                                .append("/invocations")
                                .toString())
                        .build())
                .build());
    }
}
