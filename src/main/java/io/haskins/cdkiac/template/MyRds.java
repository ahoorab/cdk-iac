package io.haskins.cdkiac.template;

import io.haskins.cdkiac.core.AppProps;
import io.haskins.cdkiac.stack.infrastructure.RDS;
import software.amazon.awscdk.App;

/**
 * Template that creates and RDS
 */
public class MyRds extends CdkIacTemplate {

    private MyRds() {
        super();
    }

    @Override
    void defineStacks(App app, AppProps appProps) {
        new RDS(app, appProps.getUniqueId(), appProps);
    }

    @Override
    void setAppProperties(AppProps appProps) {
        appProps.addProp("rds_storage", "10");
        appProps.addProp("rds_ec2", "db.t2.medium");
        appProps.addProp("rds_subnet", "subnet-group");
        appProps.addProp("rds_engine", "mysql");
        appProps.addProp("rds_version", "5.7.23");
        appProps.addProp("rds_multi-az", "false");
        appProps.addProp("rds_param_group", "param-group");
    }

    public static void main(final String[] args) {
        new MyRds();
    }
}
