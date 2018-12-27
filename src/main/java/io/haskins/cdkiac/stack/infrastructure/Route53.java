package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.utils.MissingPropertyException;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.route53.CfnHostedZone;
import software.amazon.awscdk.services.route53.CfnHostedZoneProps;
import software.amazon.awscdk.services.route53.CfnRecordSet;
import software.amazon.awscdk.services.route53.CfnRecordSetProps;

import java.util.Collections;

public class Route53 extends CdkIacStack {

    public Route53(final App parent,
                      final String name,
                      final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private Route53(final App parent,
                       final String name,
                       final StackProps props,
                       final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        try {
            CfnHostedZone zone = new CfnHostedZone(this, "HostedZone", CfnHostedZoneProps.builder()
                    .withName("example.com")
                    .withHostedZoneConfig(CfnHostedZone.HostedZoneConfigProperty.builder()
                            .withComment("My hosted zone for example.com")
                            .build())
                    .build());

            new CfnRecordSet(this, "Mail", CfnRecordSetProps.builder()
                    .withHostedZoneId(zone.getHostedZoneId())
                    .withName("mail.example.com")
                    .withType("A")
                    .withTtl("300")
                    .withResourceRecords(Collections.singletonList("127.0.0.1"))
                    .build());

            new CfnRecordSet(this, "DnsRecords", CfnRecordSetProps.builder()
                    .withAliasTarget(CfnRecordSet.AliasTargetProperty.builder()
                            .withDnsName(String.format("%s.eu-west-1.elasticbeanstalk.com", uniqueId))
                            .withEvaluateTargetHealth(false)
                            .withHostedZoneId("Z2NYPWQ7DFZAZH")
                            .build())
                    .withType("A")
                    .withName(String.format("%s.example.com.", uniqueId))
                    .withHostedZoneId(appProps.getPropAsString("hosted_zone"))
                    .build());

        } catch (MissingPropertyException e) {
            throw new StackException(e.getMessage());
        }
    }
}
