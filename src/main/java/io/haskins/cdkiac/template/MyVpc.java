package io.haskins.cdkiac.template;

import io.haskins.cdkiac.stack.infrastructure.VpcWithPeering;
import software.amazon.awscdk.App;

/**
 * Template that create and configures a VPC
 */
public class MyVpc extends CdkIacTemplate {

    private MyVpc() {
        super();
    }

    @Override
    void defineStacks(App app) {
        new VpcWithPeering(app, appProps.getUniqueId(), appProps);
    }

    @Override
    void setAppProperties() {
        appProps.addProp("vpc_cidr", "10.1.0.0/16");
        appProps.addProp("vpc_peer_id", "vpc-fff4e79b");
        appProps.addProp("vpc_peer_rt", "rtb-0a80336d");
    }

    public static void main(final String[] args) {
        new MyVpc();
    }
}
