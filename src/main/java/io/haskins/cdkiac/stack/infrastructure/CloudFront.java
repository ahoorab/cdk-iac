package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cloudfront.CfnDistribution;
import software.amazon.awscdk.services.cloudfront.CfnDistributionProps;
import software.amazon.awscdk.services.s3.CfnBucket;
import software.amazon.awscdk.services.s3.CfnBucketProps;

import java.util.Arrays;
import java.util.Collections;

public class CloudFront extends CdkIacStack {

    public CloudFront(final App parent,
                   final String name,
                   final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private CloudFront(final App parent,
                    final String name,
                    final StackProps props,
                    final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        CfnBucket bucket = new CfnBucket(this, "TargetOriginBucket", CfnBucketProps.builder()
                .withBucketName(uniqueId)
                .withVersioningConfiguration(CfnBucket.VersioningConfigurationProperty.builder()
                        .withStatus("Enabled")
                        .build())
                .build());

        new CfnDistribution(this, "CfDistribution", CfnDistributionProps.builder()
                .withDistributionConfig(CfnDistribution.DistributionConfigProperty.builder()
                        .withAliases(Collections.singletonList("assets.example.com"))
                        .withEnabled(true)
                        .withDefaultRootObject("index.html")
                        .withDefaultCacheBehavior(CfnDistribution.DefaultCacheBehaviorProperty.builder()
                                .withAllowedMethods(Arrays.asList("GET","HEAD"))
                                .withCachedMethods(Arrays.asList("GET","HEAD"))
                                .withForwardedValues(CfnDistribution.ForwardedValuesProperty.builder()
                                        .withQueryString(false)
                                        .build())
                                .withTargetOriginId(bucket.getBucketArn())
                                .withViewerProtocolPolicy("https-only")
                                .build())
                        .build())
                .build());

    }
}
