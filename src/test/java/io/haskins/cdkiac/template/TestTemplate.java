package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import software.amazon.awscdk.App;

class TestTemplate extends CdkIacTemplate {

    public TestTemplate() throws TemplateException {
        super();
    }

    void defineStacks(App app) throws MissingPropertyException, StackException {
        /*
         * Not required for any tests at this time
         */
    }
}
