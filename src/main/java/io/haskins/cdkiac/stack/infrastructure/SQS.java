package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sns.CfnSubscription;
import software.amazon.awscdk.services.sns.CfnSubscriptionProps;
import software.amazon.awscdk.services.sns.CfnTopic;
import software.amazon.awscdk.services.sns.CfnTopicProps;
import software.amazon.awscdk.services.sqs.CfnQueue;
import software.amazon.awscdk.services.sqs.CfnQueueProps;

import java.util.Collections;

public class SQS  extends CdkIacStack {

    public SQS(final App parent,
               final String name,
               final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private SQS(final App parent,
                final String name,
                final StackProps props,
                final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        CfnQueue queue = new CfnQueue(this, "Queue", CfnQueueProps.builder()
                .withQueueName(uniqueId)
                .withVisibilityTimeout(300)
                .build());

        CfnTopic topic = new CfnTopic(this, "Topic", CfnTopicProps.builder()
                .withTopicName(uniqueId)
                .withDisplayName(uniqueId)
                .withSubscription(Collections.singletonList(queue.getQueueArn()))
                .build());

        new CfnSubscription(this, "TopicSubcription", CfnSubscriptionProps.builder()
                .withTopicArn(topic.getTopicArn())
                .withEndpoint("test@test.com")
                .withProtocol("email")
                .build());
    }
}