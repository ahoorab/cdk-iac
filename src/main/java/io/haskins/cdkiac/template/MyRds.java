package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.infrastructure.RDS;
import software.amazon.awscdk.App;

public class MyRds extends CdkIacApp {

    private MyRds(String[] args) {
        super(args);
    }

    void defineStacks(App app) {
        new RDS(app, getUniqueId(), appProps);
    }

    void setAppProperties() {
        appProps.addProp("rds_storage", "10");
        appProps.addProp("rds_ec2", "db.t2.medium");
        appProps.addProp("rds_subnet", "subnet-group");
        appProps.addProp("rds_engine", "mysql");
        appProps.addProp("rds_version", "5.7.23");
        appProps.addProp("rds_multi-az", "false");
        appProps.addProp("rds_param_group", "param-group");
    }

    public static void main(final String[] args) {
        new MyRds(args);
    }
}
