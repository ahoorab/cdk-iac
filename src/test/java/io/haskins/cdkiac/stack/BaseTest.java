package io.haskins.cdkiac.stack;

import io.haskins.cdkiac.core.AppProps;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * <p>Abstract class that all tests should implement</p>
 *
 * <p>Provides functionality such as AppProps, and a HashMap to Yaml converter</p>
 */
public class BaseTest {

    protected final AppProps appProps = new AppProps();

    private final DumperOptions options = new DumperOptions();
    private Yaml yaml;

    public void setup() {
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
    }

    protected String createYaml(Object cdkOutput) {
        return yaml.dump(cdkOutput);
    }
}
