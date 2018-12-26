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

    /**
     * Exposes AppProps
     */
    protected final AppProps appProps;

    /**
     * The unique id of the application
     */
    protected final String uniqueId;

    /**
     * The name that will be given to the stack
     */
    protected final String stackName;

    /**
     * This method is where you define your stack resources
     * @throws StackException If there was a problem generating the stack
     */
    protected abstract void defineResources() throws StackException;

    /**
     *
     * @param parent An instance of the CDK App
     * @param name The name of the stack
     * @param props An instance of AppProps
     * @param appProperties An instance of StackProps
     * @throws StackException If there was a problem generating the stack
     */
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
