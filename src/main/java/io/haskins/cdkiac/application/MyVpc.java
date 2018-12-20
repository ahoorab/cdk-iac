package io.haskins.cdkiac.application;

import io.haskins.cdkiac.stack.infrastructure.VpcWithPeering;
import software.amazon.awscdk.App;

import java.io.IOException;

public class MyVpc extends AbstractApp {

    private MyVpc(String[] args) throws IOException {

        populateAppProps(args);
        setAppProperties();

        App app = new App();
        new VpcWithPeering(app, getUniqueId(), appProps);
        app.run();
    }

    private void setAppProperties() {
        appProps.addProp("app_id", "vpc");
        appProps.addProp("vpc_cidr", "10.1.0.0/16");
        appProps.addProp("vpc_peer_id", "vpc-fff4e79b");
        appProps.addProp("vpc_peer_rt", "rtb-0a80336d");
    }

    public static void main(final String[] args) throws IOException {
        new MyVpc(args);
    }
}