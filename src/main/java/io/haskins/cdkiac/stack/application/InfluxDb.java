package io.haskins.cdkiac.stack.application;

import java.util.Base64;

import io.haskins.cdkiac.core.AppProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import software.amazon.awscdk.services.autoscaling.cloudformation.AutoScalingGroupResource;
import software.amazon.awscdk.services.autoscaling.cloudformation.AutoScalingGroupResourceProps;
import software.amazon.awscdk.services.autoscaling.cloudformation.LaunchConfigurationResource;
import software.amazon.awscdk.services.autoscaling.cloudformation.LaunchConfigurationResourceProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.cloudformation.EIPResource;
import software.amazon.awscdk.services.ec2.cloudformation.VolumeResource;
import software.amazon.awscdk.services.ec2.cloudformation.VolumeResourceProps;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.iam.cloudformation.InstanceProfileResource;
import software.amazon.awscdk.services.iam.cloudformation.InstanceProfileResourceProps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InfluxDb extends Stack {

    private VpcNetworkRef vpc;

    private AppProps appProps;

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

        EIPResource eip = configureEIP();

        VolumeResource ebs = configureEBSVolume();

        InstanceProfileResource instanceProfile = configureIamRole();

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

    private EIPResource configureEIP() {

        return new EIPResource(this, "influxdbeip");
    }

    private VolumeResource configureEBSVolume() {

        return new VolumeResource(this, "influxdbebs", VolumeResourceProps.builder()
                .withSize(200)
                .withVolumeType("gp2")
                .withAvailabilityZone("eu-west-1a")
                .build());
    }

    private InstanceProfileResource configureIamRole() {

        Map<String, PolicyDocument> policies = new HashMap<>();
        policies.put("ec2", new PolicyDocument().addStatement(new PolicyStatement().allow().addResource("*").addActions("ec2:AssociateAddress", "ec2:AttachVolume")));

        Role role = new Role(this, "influxdbrole", RoleProps.builder()
                .withRoleName("influxdb-role")
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .withInlinePolicies(policies)
                .build());

        return new InstanceProfileResource(this, "influxdbinstanceprofile", InstanceProfileResourceProps.builder()
                .withInstanceProfileName("influxdb-instanceprofile")
                .withPath("/")
                .withRoles(Collections.singletonList(role.getRoleName()))
                .build());
    }

    private void configureAutoScaling(
            InstanceProfileResource instanceProfile,
            SecurityGroup sg,
            EIPResource eip,
            VolumeResource ebs) {

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

        LaunchConfigurationResource lc = new LaunchConfigurationResource(this, "influxdbaslc", LaunchConfigurationResourceProps.builder()
                .withAssociatePublicIpAddress(true)
                .withEbsOptimized(false)
                .withImageId("ami-09f0b8b3e41191524")
                .withInstanceType("t2.medium")
                .withIamInstanceProfile(instanceProfile.getInstanceProfileArn())
                .withKeyName(appProps.getPropAsString("keypair"))
                .withSecurityGroups(Collections.singletonList(sg.getSecurityGroupId()))
                .withUserData(new String(encodedBytes))
                .build());

        new AutoScalingGroupResource(this, "influxdbasg", AutoScalingGroupResourceProps.builder()
                .withAutoScalingGroupName("influxdb-as-g")
                .withDesiredCapacity("1")
                .withLaunchConfigurationName(lc.getLaunchConfigurationName())
                .withMaxSize("1")
                .withMinSize("1")
                .withVpcZoneIdentifier(appProps.getPropAsObjectList("ec2Subnets"))
                .build());
    }
}
