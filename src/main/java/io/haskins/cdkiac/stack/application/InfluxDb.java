package io.haskins.cdkiac.stack.application;

import java.util.Base64;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import software.amazon.awscdk.services.autoscaling.CfnAutoScalingGroup;
import software.amazon.awscdk.services.autoscaling.CfnAutoScalingGroupProps;
import software.amazon.awscdk.services.autoscaling.CfnLaunchConfiguration;
import software.amazon.awscdk.services.autoscaling.CfnLaunchConfigurationProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InfluxDb extends Stack {

    private final VpcNetworkRef vpc;

    private final AppProps appProps;

    public InfluxDb(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private InfluxDb(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props);

        this.appProps = appProps;

        vpc = VpcNetworkRef.import_(this,"Vpc", VpcNetworkRefProps.builder()
                .withVpcId(appProps.getPropAsString("vpcId"))
                .withAvailabilityZones(appProps.getPropAsStringList("availabilityZones"))
                .withPublicSubnetIds(appProps.getPropAsStringList("elbSubnets"))
                .withPrivateSubnetIds(appProps.getPropAsStringList("ec2Subnets"))
                .build());

        SecurityGroup sg = configureSecurityGroup();

        CfnEIP eip = configureEIP();

        CfnVolume ebs = configureEBSVolume();

        CfnInstanceProfile instanceProfile = configureIamRole();

        configureAutoScaling(instanceProfile, sg, eip, ebs);
    }

    private SecurityGroup configureSecurityGroup() {

        SecurityGroup sg = new SecurityGroup(this,"infuxdbsg", SecurityGroupProps.builder()
                .withAllowAllOutbound(true)
                .withDescription("infuxdb-sg")
                .withGroupName("infuxdb-sg")
                .withVpc(vpc)
                .build());

        sg.addIngressRule(new CidrIPv4(appProps.getPropAsString("myCidr")), new TcpPort(8888));

        return sg;
    }

    private CfnEIP configureEIP() {

        return new CfnEIP(this, "influxdbeip");
    }

    private CfnVolume configureEBSVolume() {

        return new CfnVolume(this, "influxdbebs", CfnVolumeProps.builder()
                .withSize(200)
                .withVolumeType("gp2")
                .withAvailabilityZone("eu-west-1a")
                .build());
    }

    private CfnInstanceProfile configureIamRole() {

        Map<String, PolicyDocument> policies = new HashMap<>();
        policies.put("ec2", new PolicyDocument().addStatement(new PolicyStatement().allow().addResource("*").addActions("ec2:AssociateAddress", "ec2:AttachVolume")));

        Role role = new Role(this, "influxdbrole", RoleProps.builder()
                .withRoleName("influxdb-role")
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .withInlinePolicies(policies)
                .build());

        return new CfnInstanceProfile(this, "influxdbinstanceprofile", CfnInstanceProfileProps.builder()
                .withInstanceProfileName("influxdb-instanceprofile")
                .withPath("/")
                .withRoles(Collections.singletonList(role.getRoleName()))
                .build());
    }

    private void configureAutoScaling(
            CfnInstanceProfile instanceProfile,
            SecurityGroup sg,
            CfnEIP eip,
            CfnVolume ebs) {

        StringBuilder userData = new StringBuilder();
        userData.append("#!/bin/bash -xe\n");
        userData.append("apt-get update && apt-get upgrade -y && apt-get install awscli -y\n");
        userData.append("INSTANCEID=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)\n");
        userData.append("aws --region eu-west-1 ec2 associate-address --instance-id $INSTANCEID --allocation-id ").append(eip.getEipAllocationId()).append("\n");
        userData.append("if ! mount | grep /var/lib/influxdb > /dev/nul; then rm -rf /var/lib/influxdb; fi\n");
        userData.append("aws ec2 attach-volume --region eu-west-1 --volume-id ").append(ebs.getVolumeId()).append(" --instance-id $INSTANCEID --device /dev/xvdh\n");
        userData.append("sleep 15\n");
        userData.append("mkdir /var/lib/influxdb\n");
        userData.append("mount /dev/xvdh /var/lib/influxdb || if find /var/lib/influxdb -maxdepth 0 -empty | read v; then mkfs -t ext3 /dev/xvdh && mount /dev/xvdh /var/lib/influxdb; fi\n");
        userData.append("wget https://dl.influxdata.com/influxdb/releases/influxdb_1.6.4_amd64.deb\n");
        userData.append("dpkg -i influxdb_1.6.4_amd64.deb\n");
        userData.append("chown influxdb:influxdb /var/lib/influxdb\n");
        userData.append("wget https://dl.influxdata.com/chronograf/releases/chronograf_1.6.2_amd64.deb\n");
        userData.append("dpkg -i chronograf_1.6.2_amd64.deb\n");
        userData.append("service influxdb restart && service chronograf restart\n");

        byte[] encodedBytes = Base64.getEncoder().encode(userData.toString().getBytes());

        CfnLaunchConfiguration lc = new CfnLaunchConfiguration(this, "influxdbaslc", CfnLaunchConfigurationProps.builder()
                .withAssociatePublicIpAddress(true)
                .withEbsOptimized(false)
                .withImageId("ami-09f0b8b3e41191524")
                .withInstanceType("t2.medium")
                .withIamInstanceProfile(instanceProfile.getInstanceProfileArn())
                .withKeyName(appProps.getPropAsString("keypair"))
                .withSecurityGroups(Collections.singletonList(sg.getSecurityGroupId()))
                .withUserData(new String(encodedBytes))
                .build());

        new CfnAutoScalingGroup(this, "influxdbasg", CfnAutoScalingGroupProps.builder()
                .withAutoScalingGroupName("influxdb-as-g")
                .withDesiredCapacity("1")
                .withLaunchConfigurationName(lc.getLaunchConfigurationName())
                .withMaxSize("1")
                .withMinSize("1")
                .withVpcZoneIdentifier(appProps.getPropAsObjectList("ec2Subnets"))
                .build());
    }
}
