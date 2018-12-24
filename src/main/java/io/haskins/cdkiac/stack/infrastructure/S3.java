package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.CfnBucket;
import software.amazon.awscdk.services.s3.CfnBucketProps;

import java.util.*;

public class S3 extends CdkIacStack {

    public S3(final App parent,
              final String name,
              final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private S3(final App parent,
               final String name,
               final StackProps props,
               final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        /*
         * Bucket with Versioning
         */
        new CfnBucket(this, "VersionedBucket", CfnBucketProps.builder()
                .withBucketName(uniqueId)
                .withVersioningConfiguration(CfnBucket.VersioningConfigurationProperty.builder()
                        .withStatus("Enabled")
                        .build())
                .build());

        /*
          Bucket configure for Static Hosting
         */
        List<Object> corsRules = Collections.singletonList(
                new CfnBucket.CorsRuleProperty.Builder()
                        .withAllowedHeaders(Collections.singletonList("Authorization"))
                        .withAllowedMethods(Collections.singletonList("GET"))
                        .withAllowedOrigins(Collections.singletonList("*"))
                        .withMaxAge(3000).build()
        );

        new CfnBucket(this, "StaticHostBucket", CfnBucketProps.builder()
                .withBucketName(uniqueId)
                .withWebsiteConfiguration(CfnBucket.WebsiteConfigurationProperty.builder()
                        .withIndexDocument("index.html")
                        .withErrorDocument("error.html")
                        .build())
                .withCorsConfiguration(CfnBucket.CorsConfigurationProperty.builder()
                        .withCorsRules(corsRules)
                        .build())
                .build());
    }
}
