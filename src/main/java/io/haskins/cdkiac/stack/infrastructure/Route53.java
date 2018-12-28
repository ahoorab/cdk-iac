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
