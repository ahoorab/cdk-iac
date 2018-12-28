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

package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.utils.IamPolicyGenerator;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.CfnAccountProps;
import software.amazon.awscdk.services.apigateway.CfnDomainName;
import software.amazon.awscdk.services.apigateway.CfnDomainNameProps;
import software.amazon.awscdk.services.certificatemanager.CfnCertificate;
import software.amazon.awscdk.services.certificatemanager.CfnCertificateProps;
import software.amazon.awscdk.services.iam.CfnRole;
import software.amazon.awscdk.services.iam.CfnRoleProps;
import software.amazon.awscdk.services.apigateway.CfnAccount;

import java.util.Collections;

public class ApiGateway extends CdkIacStack {

    public ApiGateway(final App parent,
               final String name,
               final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private ApiGateway(final App parent,
                final String name,
                final StackProps props,
                final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        CfnRole appRole = new CfnRole(this, "ApiGateayRole", CfnRoleProps.builder()
                .withRoleName(uniqueId)
                .withPath("/")
                .withAssumeRolePolicyDocument(IamPolicyGenerator.getServiceTrustPolicy("apigateway.amazonaws.com"))
                .withManagedPolicyArns(Collections.singletonList("arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"))
                .build());

        new CfnAccount(this, "ApiGatewayAccount", CfnAccountProps.builder()
                .withCloudWatchRoleArn(appRole.getRoleArn())
                .build());

        CfnCertificate certificate = new CfnCertificate(this, "Certificate", CfnCertificateProps.builder()
                .withDomainName("example.com")
                .build());

        new CfnDomainName(this, "ApiGatewayDomainName", CfnDomainNameProps.builder()
                .withCertificateArn(certificate.getCertificateArn())
                .withDomainName("example.com")
                .build());
    }
}
