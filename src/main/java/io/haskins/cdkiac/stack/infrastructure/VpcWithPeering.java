package io.haskins.cdkiac.stack.infrastructure;

import io.haskins.cdkiac.stack.StackException;
import io.haskins.cdkiac.utils.MissingPropertyException;
import io.haskins.cdkiac.utils.AppProps;
import io.haskins.cdkiac.stack.CdkIacStack;
import io.haskins.cdkiac.stack.infrastructure.hack.VpcBastionCloudFormationHack;
import io.haskins.cdkiac.stack.infrastructure.hack.VpcNatCloudFormationHack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.autoscaling.CfnAutoScalingGroup;
import software.amazon.awscdk.services.autoscaling.CfnAutoScalingGroupProps;
import software.amazon.awscdk.services.autoscaling.CfnLaunchConfiguration;
import software.amazon.awscdk.services.autoscaling.CfnLaunchConfigurationProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.cloudformation.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.logs.CfnLogGroup;
import software.amazon.awscdk.services.logs.CfnLogGroupProps;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VpcWithPeering extends CdkIacStack {

    public VpcWithPeering(final App parent,
                          final String name,
                          final AppProps appProps) throws StackException {

        this(parent, name, null, appProps);
    }

    private VpcWithPeering(final App parent,
                           final String name,
                           final StackProps props,
                           final AppProps appProps) throws StackException {

        super(parent, name, props, appProps);
    }

    protected void defineResources() throws StackException {

        try {

            CfnVPC vpc = new CfnVPC(this, "VPC", CfnVPCProps.builder()
                    .withCidrBlock(appProps.getPropAsString("vpc_cidr"))
                    .withEnableDnsHostnames(true)
                    .withEnableDnsSupport(true)
                    .withInstanceTenancy("default")
                    .build());


            CfnSubnet subnetPrivateA = new CfnSubnet(this, "SubnetPrivateA", CfnSubnetProps.builder()
                    .withAvailabilityZone("eu-west-1a")
                    .withCidrBlock("10.1.0.0/20")
                    .withVpcId(vpc.getVpcId())
                    .build());
            CfnSubnet subnetPrivateB = new CfnSubnet(this, "SubnetPrivateB", CfnSubnetProps.builder()
                    .withAvailabilityZone("eu-west-1b")
                    .withCidrBlock("10.1.16.0/20")
                    .withVpcId(vpc.getVpcId())
                    .build());
            CfnSubnet subnetPrivateC = new CfnSubnet(this, "SubnetPrivateC", CfnSubnetProps.builder()
                    .withAvailabilityZone("eu-west-1c")
                    .withCidrBlock("10.1.32.0/20")
                    .withVpcId(vpc.getVpcId())
                    .build());

            CfnSubnet subnetPublicA = new CfnSubnet(this, "SubnetPublicA", CfnSubnetProps.builder()
                    .withAvailabilityZone("eu-west-1a")
                    .withCidrBlock("10.1.48.0/20")
                    .withMapPublicIpOnLaunch(true)
                    .withVpcId(vpc.getVpcId())
                    .build());
            CfnSubnet subnetPublicB = new CfnSubnet(this, "SubnetPublicB", CfnSubnetProps.builder()
                    .withAvailabilityZone("eu-west-1b")
                    .withCidrBlock("10.1.64.0/20")
                    .withMapPublicIpOnLaunch(true)
                    .withVpcId(vpc.getVpcId())
                    .build());
            CfnSubnet subnetPublicC = new CfnSubnet(this, "SubnetPublicC", CfnSubnetProps.builder()
                    .withAvailabilityZone("eu-west-1c")
                    .withCidrBlock("10.1.80.0/20")
                    .withMapPublicIpOnLaunch(true)
                    .withVpcId(vpc.getVpcId())
                    .build());


            CfnRouteTable rtPrivate = new CfnRouteTable(this, "RouteTablePrivate", CfnRouteTableProps.builder()
                    .withVpcId(vpc.getVpcId())
                    .build());

            CfnRouteTable rtPublic = new CfnRouteTable(this, "RouteTablePublic", CfnRouteTableProps.builder()
                    .withVpcId(vpc.getVpcId())
                    .build());


            new CfnSubnetRouteTableAssociation(this, "RouteTableAssociationPrivateA", CfnSubnetRouteTableAssociationProps.builder()
                    .withRouteTableId(rtPrivate.getRouteTableId())
                    .withSubnetId(subnetPrivateA.getSubnetId())
                    .build());
            new CfnSubnetRouteTableAssociation(this, "RouteTableAssociationPrivateB", CfnSubnetRouteTableAssociationProps.builder()
                    .withRouteTableId(rtPrivate.getRouteTableId())
                    .withSubnetId(subnetPrivateB.getSubnetId())
                    .build());
            new CfnSubnetRouteTableAssociation(this, "RouteTableAssociationPrivateC", CfnSubnetRouteTableAssociationProps.builder()
                    .withRouteTableId(rtPrivate.getRouteTableId())
                    .withSubnetId(subnetPrivateC.getSubnetId())
                    .build());

            new CfnSubnetRouteTableAssociation(this, "RouteTableAssociationPublicA", CfnSubnetRouteTableAssociationProps.builder()
                    .withRouteTableId(rtPublic.getRouteTableId())
                    .withSubnetId(subnetPublicA.getSubnetId())
                    .build());
            new CfnSubnetRouteTableAssociation(this, "RouteTableAssociationPublicB", CfnSubnetRouteTableAssociationProps.builder()
                    .withRouteTableId(rtPublic.getRouteTableId())
                    .withSubnetId(subnetPublicB.getSubnetId())
                    .build());
            new CfnSubnetRouteTableAssociation(this, "RouteTableAssociationPublicC", CfnSubnetRouteTableAssociationProps.builder()
                    .withRouteTableId(rtPublic.getRouteTableId())
                    .withSubnetId(subnetPublicC.getSubnetId())
                    .build());

            CfnNetworkAcl privateNacl = new CfnNetworkAcl(this, "NetworkAclPrivate", CfnNetworkAclProps.builder()
                    .withVpcId(vpc.getVpcId())
                    .build());
            CfnNetworkAcl publicNacl = new CfnNetworkAcl(this, "NetworkAclPublic", CfnNetworkAclProps.builder()
                    .withVpcId(vpc.getVpcId())
                    .build());


            new CfnSubnetNetworkAclAssociation(this, "SubnetNetworkAclAssociationPrivateA", CfnSubnetNetworkAclAssociationProps.builder()
                    .withNetworkAclId(privateNacl.getNetworkAclName())
                    .withSubnetId(subnetPrivateA.getSubnetId())
                    .build());
            new CfnSubnetNetworkAclAssociation(this, "SubnetNetworkAclAssociationPrivateB", CfnSubnetNetworkAclAssociationProps.builder()
                    .withNetworkAclId(privateNacl.getNetworkAclName())
                    .withSubnetId(subnetPrivateB.getSubnetId())
                    .build());
            new CfnSubnetNetworkAclAssociation(this, "SubnetNetworkAclAssociationPrivateC", CfnSubnetNetworkAclAssociationProps.builder()
                    .withNetworkAclId(privateNacl.getNetworkAclName())
                    .withSubnetId(subnetPrivateB.getSubnetId())
                    .build());

            new CfnSubnetNetworkAclAssociation(this, "SubnetNetworkAclAssociationPublicA", CfnSubnetNetworkAclAssociationProps.builder()
                    .withNetworkAclId(publicNacl.getNetworkAclName())
                    .withSubnetId(subnetPublicA.getSubnetId())
                    .build());
            new CfnSubnetNetworkAclAssociation(this, "SubnetNetworkAclAssociationPublicB", CfnSubnetNetworkAclAssociationProps.builder()
                    .withNetworkAclId(publicNacl.getNetworkAclName())
                    .withSubnetId(subnetPublicB.getSubnetId())
                    .build());
            new CfnSubnetNetworkAclAssociation(this, "SubnetNetworkAclAssociationPublicC", CfnSubnetNetworkAclAssociationProps.builder()
                    .withNetworkAclId(publicNacl.getNetworkAclName())
                    .withSubnetId(subnetPublicC.getSubnetId())
                    .build());


            CfnVPCPeeringConnection peer = new CfnVPCPeeringConnection(this, "Peer", CfnVPCPeeringConnectionProps.builder()
                    .withPeerVpcId(appProps.getPropAsString("vpc_peer_id"))
                    .withVpcId(vpc.getVpcId())
                    .withPeerOwnerId(appProps.getPropAsString("accountid"))
                    .build());

            new CfnRoute(this, "PeeringRoute1", CfnRouteProps.builder()
                    .withDestinationCidrBlock("10.0.0.0/16")
                    .withRouteTableId(rtPrivate.getRouteTableId())
                    .withVpcPeeringConnectionId(peer.getVpcPeeringConnectionName())
                    .build());
            new CfnRoute(this, "PeeringRoute2", CfnRouteProps.builder()
                    .withDestinationCidrBlock("10.0.0.0/16")
                    .withRouteTableId(rtPublic.getRouteTableId())
                    .withVpcPeeringConnectionId(peer.getVpcPeeringConnectionName())
                    .build());
            new CfnRoute(this, "PeeringRoute3", CfnRouteProps.builder()
                    .withDestinationCidrBlock("10.1.0.0/16")
                    .withRouteTableId(appProps.getPropAsString("vpc_peer_rt"))
                    .withVpcPeeringConnectionId(peer.getVpcPeeringConnectionName())
                    .build());


            CfnInternetGateway CfnInternetGateway = new CfnInternetGateway(this, "InternetGateway");

            new CfnVPCGatewayAttachment(this, "VpcGatewayAttachment", CfnVPCGatewayAttachmentProps.builder()
                    .withVpcId(vpc.getVpcId())
                    .withInternetGatewayId(CfnInternetGateway.getInternetGatewayName())
                    .build());


            new CfnNetworkAclEntry(this, "NetworkAclEntryInPublicAllowAll", CfnNetworkAclEntryProps.builder()
                    .withCidrBlock("0.0.0.0/0")
                    .withEgress(false)
                    .withNetworkAclId(publicNacl.getNetworkAclName())
                    .withProtocol(-1)
                    .withRuleAction("allow")
                    .withRuleNumber(99)
                    .build());
            new CfnNetworkAclEntry(this, "NetworkAclEntryOutPublicAllowAll", CfnNetworkAclEntryProps.builder()
                    .withCidrBlock("0.0.0.0/0")
                    .withEgress(true)
                    .withNetworkAclId(publicNacl.getNetworkAclName())
                    .withProtocol(-1)
                    .withRuleAction("allow")
                    .withRuleNumber(99)
                    .build());
            new CfnNetworkAclEntry(this, "NetworkAclEntryInPrivateAllowAll", CfnNetworkAclEntryProps.builder()
                    .withCidrBlock("0.0.0.0/0")
                    .withEgress(false)
                    .withNetworkAclId(privateNacl.getNetworkAclName())
                    .withProtocol(-1)
                    .withRuleAction("allow")
                    .withRuleNumber(99)
                    .build());
            new CfnNetworkAclEntry(this, "NetworkAclEntryOutPrivateAllowAll", CfnNetworkAclEntryProps.builder()
                    .withCidrBlock("0.0.0.0/0")
                    .withEgress(true)
                    .withNetworkAclId(privateNacl.getNetworkAclName())
                    .withProtocol(-1)
                    .withRuleAction("allow")
                    .withRuleNumber(99)
                    .build());


            CfnEIP natEip = new CfnEIP(this, "NatEip");


            new CfnLogGroup(this, "NatLogGroup", CfnLogGroupProps.builder()
                    .withLogGroupName("vpc-nat-logs")
                    .withRetentionInDays(14)
                    .build());

            CfnSecurityGroup bastionSg = new CfnSecurityGroup(this,"BastionSecurityGroup", CfnSecurityGroupProps.builder()
                    .withGroupName("bastion")
                    .withGroupDescription("bastion")
                    .withVpcId(vpc.getVpcId())
                    .withSecurityGroupIngress(
                            Collections.singletonList(
                                    SecurityGroupIngressResourceProps.builder().withCidrIp("127.0.0.1/32").withFromPort(22).withToPort(22).withIpProtocol("tcp").build())
                    )
                    .build());

            CfnSecurityGroup natSG = new CfnSecurityGroup(this,"NatSecurityGroup", CfnSecurityGroupProps.builder()
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

            CfnInstanceProfile natInstanceProfile = new CfnInstanceProfile(this, "NatInstanceProfile", CfnInstanceProfileProps.builder()
                    .withInstanceProfileName("influxdb-instanceprofile")
                    .withPath("/")
                    .withRoles(Collections.singletonList(natRole.getRoleName()))
                    .build());


            CfnLaunchConfiguration natLaunch = new CfnLaunchConfiguration(this, "NATLaunchConfiguration", CfnLaunchConfigurationProps.builder()
                    .withAssociatePublicIpAddress(true)
                    .withEbsOptimized(false)
                    .withIamInstanceProfile(natInstanceProfile.getInstanceProfileArn())
                    .withImageId("ami-d51b3ba6")
                    .withInstanceType("t3.micro")
                    .withKeyName(appProps.getPropAsString("keypair"))
                    .withSecurityGroups(Collections.singletonList(natSG.getSecurityGroupId()))
                    .withUserData(VpcNatCloudFormationHack.getUserData(natEip.getEipAllocationId(), rtPrivate.getRouteTableId(), stackName))
                    .build());

            natLaunch.addOverride("Metadata", VpcNatCloudFormationHack.getCloudFormationMetadata());


            new CfnAutoScalingGroup(this, "NATAutoScalingGroup", CfnAutoScalingGroupProps.builder()
                    .withAutoScalingGroupName("nat")
                    .withDesiredCapacity("1")
                    .withLaunchConfigurationName(natLaunch.getLaunchConfigurationName())
                    .withMaxSize("1")
                    .withMinSize("1")
                    .withVpcZoneIdentifier(Arrays.asList(subnetPublicA.getSubnetId(),subnetPublicB.getSubnetId(),subnetPublicC.getSubnetId()))
                    .build());


            CfnEIP bastionEip = new CfnEIP(this, "BastionEip");

            new CfnLogGroup(this, "BastionLogGroup", CfnLogGroupProps.builder()
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

            CfnInstanceProfile bastionInstanceProfile = new CfnInstanceProfile(this, "BastionInstanceProfile", CfnInstanceProfileProps.builder()
                    .withInstanceProfileName("bastion")
                    .withPath("/")
                    .withRoles(Collections.singletonList(bastionRole.getRoleName()))
                    .build());


            CfnLaunchConfiguration bastionLaunch = new CfnLaunchConfiguration(this, "BastionLaunchConfiguration", CfnLaunchConfigurationProps.builder()
                    .withAssociatePublicIpAddress(true)
                    .withEbsOptimized(false)
                    .withIamInstanceProfile(bastionInstanceProfile.getInstanceProfileArn())
                    .withImageId("ami-d51b3ba6")
                    .withInstanceType("t3.micro")
                    .withKeyName(appProps.getPropAsString("keypair"))
                    .withSecurityGroups(Collections.singletonList(bastionSg.getSecurityGroupId()))
                    .withUserData(VpcBastionCloudFormationHack.getUserData(bastionEip.getEipAllocationId(), stackName))
                    .build());

            natLaunch.addOverride("Metadata", VpcBastionCloudFormationHack.getCloudFormationMetadata());


            new CfnAutoScalingGroup(this, "BastionAutoScalingGroup", CfnAutoScalingGroupProps.builder()
                    .withAutoScalingGroupName("bastion")
                    .withDesiredCapacity("1")
                    .withLaunchConfigurationName(bastionLaunch.getLaunchConfigurationName())
                    .withMaxSize("1")
                    .withMinSize("1")
                    .withVpcZoneIdentifier(Arrays.asList(subnetPublicA.getSubnetId(),subnetPublicB.getSubnetId(),subnetPublicC.getSubnetId()))
                    .build());

        } catch (MissingPropertyException e) {
            throw new StackException(e.getMessage());
        }
    }
}
