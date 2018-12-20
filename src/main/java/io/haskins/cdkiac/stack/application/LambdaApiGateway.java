package io.haskins.cdkiac.stack.application;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.cloudformation.*;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.cloudformation.FunctionResource;
import software.amazon.awscdk.services.lambda.cloudformation.FunctionResourceProps;

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

        FunctionResource lambda = new FunctionResource(this, "LambdaFunction", FunctionResourceProps.builder()
                .withFunctionName(uniqueId)
                .withRuntime(appProps.getPropAsString("runtime"))
                .withMemorySize(appProps.getPropAsInteger("memory_size"))
                .withHandler(appProps.getPropAsString("handler"))
                .withRole(appRole.getRoleName())
                .withCode(FunctionResource.CodeProperty.builder()
                        .withS3Bucket(appProps.getPropAsString("code_bucket"))
                        .withS3Key(appProps.getPropAsString("code_key"))
                        .build())
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
                .withIntegration(MethodResource.IntegrationProperty.builder()
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
