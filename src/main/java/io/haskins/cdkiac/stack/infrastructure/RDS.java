package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.core.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.CfnDBInstance;
import software.amazon.awscdk.services.rds.CfnDBInstanceProps;

import java.util.Collections;


public class RDS extends CdkIacStack {

    public RDS(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private RDS(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props, appProps);
    }

    protected void defineResources() {

        VpcNetworkRef vpc = VpcNetworkRef.import_(this,"Vpc", VpcNetworkRefProps.builder()
                .withVpcId(appProps.getPropAsString("vpc_id"))
                .withAvailabilityZones(appProps.getPropAsStringList("availability_zones"))
                .withPublicSubnetIds(appProps.getPropAsStringList("elb_subnets"))
                .withPrivateSubnetIds(appProps.getPropAsStringList("ec2_subnets"))
                .build());

        SecurityGroup sg = new SecurityGroup(this,"RdsSecurityGroup", SecurityGroupProps.builder()
                .withAllowAllOutbound(true)
                .withDescription(uniqueId)
                .withGroupName(uniqueId)
                .withVpc(vpc)
                .build());

        sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("my_cidr")), new TcpPort(3306));
        sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("vpc_cidr")), new TcpPort(3306));

        new CfnDBInstance(this, "Rds", CfnDBInstanceProps.builder()
                .withAllocatedStorage(appProps.getPropAsString("rds_storage"))
                .withStorageType("gp2")
                .withDbInstanceClass(appProps.getPropAsString("rds_ec2"))
                .withDbInstanceIdentifier(uniqueId)
                .withDbSubnetGroupName(appProps.getPropAsString("rds_subnet"))
                .withEngine(appProps.getPropAsString("rds_engine"))
                .withEngineVersion(appProps.getPropAsString("rds_version"))
                .withMasterUsername("Root")
                .withMasterUserPassword("0000") // they'll never guess that :)
                .withMultiAz(appProps.getPropAsBoolean("rds_multi"))
                .withVpcSecurityGroups(Collections.singletonList(sg.getSecurityGroupId()))
                .withDbParameterGroupName(appProps.getPropAsString("rds_param_group"))
                .build());
    }
}
