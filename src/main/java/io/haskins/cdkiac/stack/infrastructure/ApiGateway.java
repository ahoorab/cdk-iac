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
