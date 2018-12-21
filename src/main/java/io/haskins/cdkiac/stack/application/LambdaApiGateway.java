package io.haskins.cdkiac.stack.application;

import io.haskins.cdkiac.core.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.utils.IamPolicyGenerator;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.CfnFunctionProps;

public class LambdaApiGateway extends CdkIacStack {

    public LambdaApiGateway(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private LambdaApiGateway(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props, appProps);
    }

    protected void defineResources() {

        CfnRole appRole = new CfnRole(this, "LambdaRole", CfnRoleProps.builder()
                .withRoleName(uniqueId)
                .withPath("/")
                .withAssumeRolePolicyDocument(IamPolicyGenerator.getServiceTrustPolicy("lambda.amazonaws.com"))
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
