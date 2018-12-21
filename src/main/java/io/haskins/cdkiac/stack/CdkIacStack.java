package io.haskins.cdkiac.stack;

import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public abstract class CdkIacStack extends Stack {

    protected abstract void defineResources();

    protected final AppProps appProps;

    protected final String uniqueId;
    protected final String stackName;

    protected CdkIacStack(final App parent, final String name, final StackProps props, final AppProps appProperties) {

        super(parent, name, props);

        appProps = appProperties;

        uniqueId = appProps.getUniqueId();
        stackName = name;

        defineResources();
    }
}
