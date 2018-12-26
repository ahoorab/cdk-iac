package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class CloudFormation extends CdkIacStack {

    public CloudFormation(final App parent,
                   final String name,
                   final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private CloudFormation(final App parent,
                    final String name,
                    final StackProps props,
                    final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {



    }
}
