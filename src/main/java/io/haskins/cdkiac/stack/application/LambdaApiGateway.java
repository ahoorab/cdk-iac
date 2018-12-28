/*
 * MIT License
 *
 * Copyright (c) 2018 Mark Haskins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.MIT License
 */

package io.haskins.cdkiac.stack.application;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.utils.IamPolicyGenerator;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.CfnFunctionProps;

public class LambdaApiGateway extends CdkIacStack {

    public LambdaApiGateway(final App parent,
                            final String name,
                            final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private LambdaApiGateway(final App parent,
                             final String name,
                             final StackProps props,
                             final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        try {

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

        } catch (MissingPropertyException e) {
            throw new StackException(e.getMessage());
        }

    }
}
