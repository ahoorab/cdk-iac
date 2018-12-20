package io.haskins.cdkiac.stack.infrastructure;

import com.google.common.collect.ImmutableMap;
import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.CfnBucket;
import software.amazon.awscdk.services.s3.CfnBucketProps;

import java.util.*;

public class S3 extends Stack {

    public S3(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private S3(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props);

        String uniqueId = appProps.getUniqueId();

        CfnBucket normal = new CfnBucket(this, "mybucket", CfnBucketProps.builder()
                .withBucketName(uniqueId)
                .withVersioningConfiguration(CfnBucket.VersioningConfigurationProperty.builder()
                        .withStatus("Enabled")
                        .build())
                .build());

        List<Object> corsRules = Arrays.asList(
                ImmutableMap.of("AllowedHeaders",  Collections.singletonList("Authorization")),
                ImmutableMap.of("AllowedMethods",  Collections.singletonList("GET")),
                ImmutableMap.of("AllowedOrigins", Collections.singletonList("*")),
                ImmutableMap.of("MaxAge", 3000)
        );

        CfnBucket webhosting = new CfnBucket(this, "mybucket", CfnBucketProps.builder()
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
