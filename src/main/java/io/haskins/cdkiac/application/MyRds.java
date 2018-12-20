package io.haskins.cdkiac.application;

import io.haskins.cdkiac.stack.infrastructure.RDS;
import software.amazon.awscdk.App;

import java.io.IOException;

public class MyRds extends AbstractApp {

    private MyRds(String[] args) throws IOException {

        populateAppProps(args);
        setAppProperties();

        App app = new App();
        new RDS(app, getUniqueId(), appProps);
        app.run();
    }

    private void setAppProperties() {
        appProps.addProp("app_id", "my-rds");
        appProps.addProp("rds_storage", "10");
        appProps.addProp("rds_ec2", "db.t2.medium");
        appProps.addProp("rds_subnet", "subnet-group");
        appProps.addProp("rds_engine", "mysql");
        appProps.addProp("rds_version", "5.7.23");
        appProps.addProp("rds_multi-az", "false");
        appProps.addProp("rds_param_group", "param-group");
    }

    public static void main(final String[] args) throws IOException {
        new MyRds(args);
    }
}