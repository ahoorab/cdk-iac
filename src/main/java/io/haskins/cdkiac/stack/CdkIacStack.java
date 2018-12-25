package io.haskins.cdkiac.stack;

import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.utils.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

/**
 * Abstract class that all Stacks should extend
 */
public abstract class CdkIacStack extends Stack {

    protected final AppProps appProps;

    protected final String uniqueId;
    protected final String stackName;

    protected abstract void defineResources() throws StackException;


    protected CdkIacStack(final App parent,
                          final String name,
                          final StackProps props,
                          final AppProps appProperties) throws StackException {

        super(parent, name, props);

        appProps = appProperties;

        try {
            uniqueId = appProps.getUniqueId();
        } catch (MissingPropertyException e) {
            throw new StackException(e.getMessage());
        }

        stackName = name;
        defineResources();
    }
}
