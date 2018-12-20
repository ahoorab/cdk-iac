package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.core.AppProps;
import io.haskins.cdkiac.stack.infrastructure.hack.VpcBastionCloudFormationHack;
import io.haskins.cdkiac.stack.infrastructure.hack.VpcNatCloudFormationHack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.autoscaling.cloudformation.AutoScalingGroupResource;
import software.amazon.awscdk.services.autoscaling.cloudformation.AutoScalingGroupResourceProps;
import software.amazon.awscdk.services.autoscaling.cloudformation.LaunchConfigurationResource;
import software.amazon.awscdk.services.autoscaling.cloudformation.LaunchConfigurationResourceProps;
import software.amazon.awscdk.services.ec2.cloudformation.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.iam.cloudformation.InstanceProfileResource;
import software.amazon.awscdk.services.iam.cloudformation.InstanceProfileResourceProps;
import software.amazon.awscdk.services.logs.cloudformation.LogGroupResource;
import software.amazon.awscdk.services.logs.cloudformation.LogGroupResourceProps;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VpcWithPeering  extends Stack {

    public VpcWithPeering(final App parent, final String name, final AppProps appProps) {
        this(parent, name, null, appProps);
    }

    private VpcWithPeering(final App parent, final String name, final StackProps props, final AppProps appProps) {
        super(parent, name, props);


        VPCResource vpc = new VPCResource(this, "VPC", VPCResourceProps.builder()
                .withCidrBlock(appProps.getPropAsString("vpc_cidr"))
                .withEnableDnsHostnames(true)
                .withEnableDnsSupport(true)
                .withInstanceTenancy("default")
                .build());


        SubnetResource subnetPrivateA = new SubnetResource(this, "SubnetPrivateA", SubnetResourceProps.builder()
                .withAvailabilityZone("eu-west-1a")
                .withCidrBlock("10.1.0.0/20")
                .withVpcId(vpc.getVpcId())
                .build());
        SubnetResource subnetPrivateB = new SubnetResource(this, "SubnetPrivateB", SubnetResourceProps.builder()
                .withAvailabilityZone("eu-west-1b")
                .withCidrBlock("10.1.16.0/20")
                .withVpcId(vpc.getVpcId())
                .build());
        SubnetResource subnetPrivateC = new SubnetResource(this, "SubnetPrivateC", SubnetResourceProps.builder()
                .withAvailabilityZone("eu-west-1c")
                .withCidrBlock("10.1.32.0/20")
                .withVpcId(vpc.getVpcId())
                .build());

        SubnetResource subnetPublicA = new SubnetResource(this, "SubnetPublicA", SubnetResourceProps.builder()
                .withAvailabilityZone("eu-west-1a")
                .withCidrBlock("10.1.48.0/20")
                .withMapPublicIpOnLaunch(true)
                .withVpcId(vpc.getVpcId())
                .build());
        SubnetResource subnetPublicB = new SubnetResource(this, "SubnetPublicB", SubnetResourceProps.builder()
                .withAvailabilityZone("eu-west-1b")
                .withCidrBlock("10.1.64.0/20")
                .withMapPublicIpOnLaunch(true)
                .withVpcId(vpc.getVpcId())
                .build());
        SubnetResource subnetPublicC = new SubnetResource(this, "SubnetPublicC", SubnetResourceProps.builder()
                .withAvailabilityZone("eu-west-1c")
                .withCidrBlock("10.1.80.0/20")
                .withMapPublicIpOnLaunch(true)
                .withVpcId(vpc.getVpcId())
                .build());


        RouteTableResource rtPrivate = new RouteTableResource(this, "RouteTablePrivate", RouteTableResourceProps.builder()
                .withVpcId(vpc.getVpcId())
                .build());

        RouteTableResource rtPublic = new RouteTableResource(this, "RouteTablePublic", RouteTableResourceProps.builder()
                .withVpcId(vpc.getVpcId())
                .build());


        new SubnetRouteTableAssociationResource(this, "RouteTableAssociationPrivateA", SubnetRouteTableAssociationResourceProps.builder()
                .withRouteTableId(rtPrivate.getRouteTableId())
                .withSubnetId(subnetPrivateA.getSubnetId())
                .build());
        new SubnetRouteTableAssociationResource(this, "RouteTableAssociationPrivateB", SubnetRouteTableAssociationResourceProps.builder()
                .withRouteTableId(rtPrivate.getRouteTableId())
                .withSubnetId(subnetPrivateB.getSubnetId())
                .build());
        new SubnetRouteTableAssociationResource(this, "RouteTableAssociationPrivateC", SubnetRouteTableAssociationResourceProps.builder()
                .withRouteTableId(rtPrivate.getRouteTableId())
                .withSubnetId(subnetPrivateC.getSubnetId())
                .build());

        new SubnetRouteTableAssociationResource(this, "RouteTableAssociationPublicA", SubnetRouteTableAssociationResourceProps.builder()
                .withRouteTableId(rtPublic.getRouteTableId())
                .withSubnetId(subnetPublicA.getSubnetId())
                .build());
        new SubnetRouteTableAssociationResource(this, "RouteTableAssociationPublicB", SubnetRouteTableAssociationResourceProps.builder()
                .withRouteTableId(rtPublic.getRouteTableId())
                .withSubnetId(subnetPublicB.getSubnetId())
                .build());
        new SubnetRouteTableAssociationResource(this, "RouteTableAssociationPublicC", SubnetRouteTableAssociationResourceProps.builder()
                .withRouteTableId(rtPublic.getRouteTableId())
                .withSubnetId(subnetPublicC.getSubnetId())
                .build());

        NetworkAclResource privateNacl = new NetworkAclResource(this, "NetworkAclPrivate", NetworkAclResourceProps.builder()
                .withVpcId(vpc.getVpcId())
                .build());
        NetworkAclResource publicNacl = new NetworkAclResource(this, "NetworkAclPublic", NetworkAclResourceProps.builder()
                .withVpcId(vpc.getVpcId())
                .build());


        new SubnetNetworkAclAssociationResource(this, "SubnetNetworkAclAssociationPrivateA", SubnetNetworkAclAssociationResourceProps.builder()
                .withNetworkAclId(privateNacl.getNetworkAclName())
                .withSubnetId(subnetPrivateA.getSubnetId())
                .build());
        new SubnetNetworkAclAssociationResource(this, "SubnetNetworkAclAssociationPrivateB", SubnetNetworkAclAssociationResourceProps.builder()
                .withNetworkAclId(privateNacl.getNetworkAclName())
                .withSubnetId(subnetPrivateB.getSubnetId())
                .build());
        new SubnetNetworkAclAssociationResource(this, "SubnetNetworkAclAssociationPrivateC", SubnetNetworkAclAssociationResourceProps.builder()
                .withNetworkAclId(privateNacl.getNetworkAclName())
                .withSubnetId(subnetPrivateB.getSubnetId())
                .build());

        new SubnetNetworkAclAssociationResource(this, "SubnetNetworkAclAssociationPublicA", SubnetNetworkAclAssociationResourceProps.builder()
                .withNetworkAclId(publicNacl.getNetworkAclName())
                .withSubnetId(subnetPublicA.getSubnetId())
                .build());
        new SubnetNetworkAclAssociationResource(this, "SubnetNetworkAclAssociationPublicB", SubnetNetworkAclAssociationResourceProps.builder()
                .withNetworkAclId(publicNacl.getNetworkAclName())
                .withSubnetId(subnetPublicB.getSubnetId())
                .build());
        new SubnetNetworkAclAssociationResource(this, "SubnetNetworkAclAssociationPublicC", SubnetNetworkAclAssociationResourceProps.builder()
                .withNetworkAclId(publicNacl.getNetworkAclName())
                .withSubnetId(subnetPublicC.getSubnetId())
                .build());


        VPCPeeringConnectionResource peer = new VPCPeeringConnectionResource(this, "Peer", VPCPeeringConnectionResourceProps.builder()
                .withPeerVpcId(appProps.getPropAsString("vpc_peer_id"))
                .withVpcId(vpc.getVpcId())
                .withPeerOwnerId(appProps.getPropAsString("accountid"))
                .build());

        new RouteResource(this, "PeeringRoute1", RouteResourceProps.builder()
                .withDestinationCidrBlock("10.0.0.0/16")
                .withRouteTableId(rtPrivate.getRouteTableId())
                .withVpcPeeringConnectionId(peer.getVpcPeeringConnectionName())
                .build());
        new RouteResource(this, "PeeringRoute2", RouteResourceProps.builder()
                .withDestinationCidrBlock("10.0.0.0/16")
                .withRouteTableId(rtPublic.getRouteTableId())
                .withVpcPeeringConnectionId(peer.getVpcPeeringConnectionName())
                .build());
        new RouteResource(this, "PeeringRoute3", RouteResourceProps.builder()
                .withDestinationCidrBlock("10.1.0.0/16")
                .withRouteTableId(appProps.getPropAsString("vpc_peer_rt"))
                .withVpcPeeringConnectionId(peer.getVpcPeeringConnectionName())
                .build());


        InternetGatewayResource internetGatewayResource = new InternetGatewayResource(this, "InternetGateway");

        new VPCGatewayAttachmentResource(this, "VpcGatewayAttachment", VPCGatewayAttachmentResourceProps.builder()
                .withVpcId(vpc.getVpcId())
                .withInternetGatewayId(internetGatewayResource.getInternetGatewayName())
                .build());


        new NetworkAclEntryResource(this, "NetworkAclEntryInPublicAllowAll", NetworkAclEntryResourceProps.builder()
                .withCidrBlock("0.0.0.0/0")
                .withEgress(false)
                .withNetworkAclId(publicNacl.getNetworkAclName())
                .withProtocol(-1)
                .withRuleAction("allow")
                .withRuleNumber(99)
                .build());
        new NetworkAclEntryResource(this, "NetworkAclEntryOutPublicAllowAll", NetworkAclEntryResourceProps.builder()
                .withCidrBlock("0.0.0.0/0")
                .withEgress(true)
                .withNetworkAclId(publicNacl.getNetworkAclName())
                .withProtocol(-1)
                .withRuleAction("allow")
                .withRuleNumber(99)
                .build());
        new NetworkAclEntryResource(this, "NetworkAclEntryInPrivateAllowAll", NetworkAclEntryResourceProps.builder()
                .withCidrBlock("0.0.0.0/0")
                .withEgress(false)
                .withNetworkAclId(privateNacl.getNetworkAclName())
                .withProtocol(-1)
                .withRuleAction("allow")
                .withRuleNumber(99)
                .build());
        new NetworkAclEntryResource(this, "NetworkAclEntryOutPrivateAllowAll", NetworkAclEntryResourceProps.builder()
                .withCidrBlock("0.0.0.0/0")
                .withEgress(true)
                .withNetworkAclId(privateNacl.getNetworkAclName())
                .withProtocol(-1)
                .withRuleAction("allow")
                .withRuleNumber(99)
                .build());


        new EIPResource(this, "NatEip");


        new LogGroupResource(this, "NatLogGroup", LogGroupResourceProps.builder()
                .withLogGroupName("vpc-nat-logs")
                .withRetentionInDays(14)
                .build());

        SecurityGroupResource bastionSg = new SecurityGroupResource(this,"BastionSecurityGroup", SecurityGroupResourceProps.builder()
                .withGroupName("bastion")
                .withGroupDescription("bastion")
                .withVpcId(vpc.getVpcId())
                .withSecurityGroupIngress(
                        Collections.singletonList(
                                SecurityGroupIngressResourceProps.builder().withCidrIp("127.0.0.1/32").withFromPort(22).withToPort(22).withIpProtocol("tcp").build())
                )
                .build());

        SecurityGroupResource natSG = new SecurityGroupResource(this,"NatSecurityGroup", SecurityGroupResourceProps.builder()
                .withGroupName("nat")
                .withGroupDescription("nat")
                .withVpcId(vpc.getVpcId())
                .withSecurityGroupIngress(
                        Arrays.asList(
                                SecurityGroupIngressResourceProps.builder().withCidrIp("0.0.0.0/0").withFromPort(80).withToPort(80).withIpProtocol("tcp").build(),
                                SecurityGroupIngressResourceProps.builder().withSourceSecurityGroupId(bastionSg.getSecurityGroupId()).withFromPort(22).withToPort(22).withIpProtocol("tcp").build()
                        )
                )
                .build());




        Map<String, PolicyDocument> natPolicies = new HashMap<>();
        natPolicies.put("ec2", new PolicyDocument().addStatement(new PolicyStatement().allow().addResource("*").addActions("ec2:AssociateAddress", "ec2:ModifyInstanceAttribute", "ec2:CreateRoute", "ec2:ReplaceRoute")));
        natPolicies.put("logs", new PolicyDocument().addStatement(new PolicyStatement().allow().addResource("*").addActions("logs:CreateLogGroup", "logs:CreateLogStream", "logs:PutLogEvents", "logs:DescribeLogStreams")));

        Role natRole = new Role(this, "NatIamRole", RoleProps.builder()
                .withRoleName("nat")
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .withInlinePolicies(natPolicies)
                .build());

        InstanceProfileResource natInstanceProfile = new InstanceProfileResource(this, "NatInstanceProfile", InstanceProfileResourceProps.builder()
                .withInstanceProfileName("influxdb-instanceprofile")
                .withPath("/")
                .withRoles(Collections.singletonList(natRole.getRoleName()))
                .build());


        LaunchConfigurationResource natLaunch = new LaunchConfigurationResource(this, "NATLaunchConfiguration", LaunchConfigurationResourceProps.builder()
                .withAssociatePublicIpAddress(true)
                .withEbsOptimized(false)
                .withIamInstanceProfile(natInstanceProfile.getInstanceProfileArn())
                .withImageId("ami-d51b3ba6")
                .withInstanceType("t3.micro")
                .withKeyName(appProps.getPropAsString("keypair"))
                .withSecurityGroups(Collections.singletonList(natSG.getSecurityGroupId()))
                .withUserData(VpcNatCloudFormationHack.getUserData())
                .build());

        natLaunch.addOverride("Metadata", VpcNatCloudFormationHack.getCloudFormationMetadata());


        new AutoScalingGroupResource(this, "NATAutoScalingGroup", AutoScalingGroupResourceProps.builder()
                .withAutoScalingGroupName("nat")
                .withDesiredCapacity("1")
                .withLaunchConfigurationName(natLaunch.getLaunchConfigurationName())
                .withMaxSize("1")
                .withMinSize("1")
                .withVpcZoneIdentifier(Arrays.asList(subnetPublicA.getSubnetId(),subnetPublicB.getSubnetId(),subnetPublicC.getSubnetId()))
                .build());


        new EIPResource(this, "BastionEip");

        new LogGroupResource(this, "BastionLogGroup", LogGroupResourceProps.builder()
                .withLogGroupName("vpc-bastion-logs")
                .withRetentionInDays(14)
                .build());


        Map<String, PolicyDocument> bastionPolicies = new HashMap<>();
        bastionPolicies.put("ec2", new PolicyDocument().addStatement(new PolicyStatement().allow().addResource("*").addActions("ec2:AssociateAddress")));
        bastionPolicies.put("logs", new PolicyDocument().addStatement(new PolicyStatement().allow().addResource("*").addActions("logs:CreateLogGroup", "logs:CreateLogStream", "logs:PutLogEvents", "logs:DescribeLogStreams")));


        Role bastionRole = new Role(this, "BastionIamRole", RoleProps.builder()
                .withRoleName("bastion")
                .withPath("/")
                .withAssumedBy(new ServicePrincipal("ec2.amazonaws.com"))
                .withInlinePolicies(bastionPolicies)
                .build());

        InstanceProfileResource bastionInstanceProfile = new InstanceProfileResource(this, "BastionInstanceProfile", InstanceProfileResourceProps.builder()
                .withInstanceProfileName("bastion")
                .withPath("/")
                .withRoles(Collections.singletonList(bastionRole.getRoleName()))
                .build());


        LaunchConfigurationResource bastionLaunch = new LaunchConfigurationResource(this, "BastionLaunchConfiguration", LaunchConfigurationResourceProps.builder()
                .withAssociatePublicIpAddress(true)
                .withEbsOptimized(false)
                .withIamInstanceProfile(bastionInstanceProfile.getInstanceProfileArn())
                .withImageId("ami-d51b3ba6")
                .withInstanceType("t3.micro")
                .withKeyName(appProps.getPropAsString("keypair"))
                .withSecurityGroups(Collections.singletonList(bastionSg.getSecurityGroupId()))
                .withUserData(VpcBastionCloudFormationHack.getUserData())
                .build());

        natLaunch.addOverride("Metadata", VpcBastionCloudFormationHack.getCloudFormationMetadata());


        new AutoScalingGroupResource(this, "BastionAutoScalingGroup", AutoScalingGroupResourceProps.builder()
                .withAutoScalingGroupName("bastion")
                .withDesiredCapacity("1")
                .withLaunchConfigurationName(bastionLaunch.getLaunchConfigurationName())
                .withMaxSize("1")
                .withMinSize("1")
                .withVpcZoneIdentifier(Arrays.asList(subnetPublicA.getSubnetId(),subnetPublicB.getSubnetId(),subnetPublicC.getSubnetId()))
                .build());

    }
}
