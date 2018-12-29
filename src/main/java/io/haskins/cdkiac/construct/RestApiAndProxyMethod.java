package io.haskins.cdkiac.construct;

import software.amazon.awscdk.Construct;
import software.amazon.awscdk.services.apigateway.CfnResource;
import software.amazon.awscdk.services.apigateway.CfnResourceProps;
import software.amazon.awscdk.services.apigateway.CfnRestApi;
import software.amazon.awscdk.services.apigateway.CfnRestApiProps;

public class RestApiAndProxyMethod extends Construct {

    private final CfnRestApi restApi;
    private final CfnResource cfnResource;

    public RestApiAndProxyMethod(final Construct parent, final String name, RestApiAndProxyMethodProps props) {

        super(parent, name);


        restApi = new CfnRestApi(this, "RestApi", CfnRestApiProps.builder()
                .withName(props.getUniqueId())
                .build());

        cfnResource = new CfnResource(this, "CfnRestApi", CfnResourceProps.builder()
                .withPathPart("{proxy+}")
                .withRestApiId(restApi.getRestApiId())
                .withParentId(restApi.getRestApiRootResourceId())
                .build());
    }

    public CfnRestApi getRestApi() {
        return restApi;
    }

    public CfnResource getResource() {
        return cfnResource;
    }

}
