package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.CfnDBInstance;
import software.amazon.awscdk.services.rds.CfnDBInstanceProps;

import java.util.Arrays;
import java.util.Collections;

public class RDS extends Stack {

    public RDS(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private RDS(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props);

        String uniqueId = appProps.getUniqueId();

        VpcNetworkRef vpc = VpcNetworkRef.import_(this,"Vpc", VpcNetworkRefProps.builder()
                .withVpcId(appProps.getPropAsString("vpcId"))
                .withAvailabilityZones(appProps.getPropAsStringList("availabilityZones"))
                .withPublicSubnetIds(appProps.getPropAsStringList("elbSubnets"))
                .withPrivateSubnetIds(appProps.getPropAsStringList("ec2Subnets"))
                .build());

        SecurityGroup sg = new SecurityGroup(this,"RdsSecurityGroup", SecurityGroupProps.builder()
                .withAllowAllOutbound(true)
                .withDescription(uniqueId)
                .withGroupName(uniqueId)
                .withVpc(vpc)
                .build());

        sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("myCidr")), new TcpPort(3306));
        sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("vpcCidr")), new TcpPort(3306));

        CfnDBInstance rds = new CfnDBInstance(this, "Rds", CfnDBInstanceProps.builder()
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
