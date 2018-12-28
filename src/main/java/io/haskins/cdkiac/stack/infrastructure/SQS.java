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