package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.kms.CfnKey;
import software.amazon.awscdk.services.kms.CfnKeyProps;
import software.amazon.awscdk.services.kms.EncryptionKeyRef;
import software.amazon.awscdk.services.kms.EncryptionKeyRefProps;

public class KMS  extends CdkIacStack {

    public KMS(final App parent,
               final String name,
               final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private KMS(final App parent,
                final String name,
                final StackProps props,
                final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        CfnKey key = new CfnKey(this, "Key", CfnKeyProps.builder()
                .withDescription("MyKey")
                .withEnabled(true)
                .withEnableKeyRotation(true)
                .withPendingWindowInDays(14)
                .build());

    }
}
