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
