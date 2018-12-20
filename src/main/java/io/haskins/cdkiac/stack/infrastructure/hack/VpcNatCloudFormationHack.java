package io.haskins.cdkiac.stack.infrastructure.hack;

import com.google.common.collect.ImmutableMap;

import java.util.*;

/**
 * This here because CDK does not easily support CloudFormation Meta / CloudFormation::Init ... yet
 */
public class VpcNatCloudFormationHack {

    public static Map getCloudFormationMetadata() {

        Map<String, Object> init = new HashMap<>();
        init.put("configSets", ImmutableMap.of("default", Collections.singletonList("config")));

        Map<String, Object> config = new HashMap<>();
        config.put("packages", ImmutableMap.of("yum", ImmutableMap.of("awslogs", new ArrayList())));
        config.put("files", ImmutableMap.of(
                "/etc/awslogs/awscli.conf", ImmutableMap.of("content", "[default]\n region=eu-west-1\n [plugins]\n cwlogs=cwlogs","mode", "000644","owner","root","group","root"),
                "/etc/awslogs/awslogs.conf", ImmutableMap.of("content", new StringBuilder()
                        .append("[general]\n state_file = /var/lib/awslogs/agent-state\n ")
                        .append("[/var/log/messages]\n datetime_format=%b %d %H:%M:%S\n file=/var/log/messages\n log_stream_name={instance_id}/var/log/messages\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/secure]\n datetime_format=%b %d %H:%M:%S\n file=/var/log/secure\n log_stream_name = {instance_id}/var/log/secure\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/cron]\n datetime_format = %b %d %H:%M:%S\n file = /var/log/cron\n log_stream_name = {instance_id}/var/log/cron\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/cloud-init.log]\n datetime_format = %b %d %H:%M:%S\n file = /var/log/cloud-init.log\n log_stream_name = {instance_id}/var/log/cloud-init.log\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/cfn-init.log]\n datetime_format = %Y-%m-%d %H:%M:%S\n file = /var/log/cfn-init.log\n log_stream_name = {instance_id}/var/log/cfn-init.log\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/cfn-hup.log]\n datetime_format = %Y-%m-%d %H:%M:%S\n file = /var/log/cfn-hup.log\n log_stream_name = {instance_id}/var/log/cfn-hup.log\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/cfn-init-cmd.log]\n datetime_format = %Y-%m-%d %H:%M:%S\n file = /var/log/cfn-init-cmd.log\n log_stream_name = {instance_id}/var/log/cfn-init-cmd.log\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/cloud-init-output.log]\n file = /var/log/cloud-init-output.log\n log_stream_name = {instance_id}/var/log/cloud-init-output.log\n log_group_name=vpc-nat-logs\n ")
                        .append("[/var/log/dmesg]\n file = /var/log/dmesg\n log_stream_name = {instance_id}/var/log/dmesg\n log_group_name=vpc-nat-logs\n ").toString(),
                        "mode", "000644","owner","root","group","root"
                ),
                "/etc/cfn/cfn-hup.conf", ImmutableMap.of("content","[main]\n  stack={ \"Ref\": \"AWS::StackName\" } region=eu-west-1\n interval=1\n", "mode", "000400","owner","root","group","root"),
                "/etc/cfn/hooks.d/cfn-auto-reloader.conf", ImmutableMap.of("content", new StringBuilder()
                        .append("[cfn-auto-reloader-hook]\n")
                        .append("triggers=post.update\n")
                        .append("path=Resources.NATLaunchConfiguration.Metadata.AWS::CloudFormation::Init\n")
                        .append("action=/opt/aws/bin/cfn-init --verbose")
                        .append(" --stack { \"Ref\": \"AWS::StackName\" }")
                        .append(" --region \",{ \"Ref\": \"AWS::Region\" }")
                        .append(" --resource NATLaunchConfiguration\n")
                        .append(" runas root")
                )
        ));

        config.put("services", ImmutableMap.of("sysvinit", ImmutableMap.of(
                "awslogs", ImmutableMap.of(
                        "enabled", "true",
                        "ensureRunning", "true",
                        "packages", ImmutableMap.of("yum", ImmutableMap.of("awslogs",  new ArrayList())),
                        "files", Arrays.asList("/etc/awslogs/awslogs.conf", "/etc/awslogs/awscli.conf")),
                "cfn-hup", ImmutableMap.of(
                        "enabled", "true",
                        "ensureRunning", "true",
                        "files", Arrays.asList("/etc/cfn/cfn-hup.conf", "/etc/cfn/hooks.d/cfn-auto-reloader.conf"))
        )));

        init.put("config",config);

        Map<String, Map> metadata = new HashMap<>();
        metadata.put("AWS::CloudFormation::Init", init);

        return metadata;
    }

    public static String getUserData(String eip, String routeTable, String stackName) {

        StringBuilder userData = new StringBuilder();
        userData.append("#!/bin/bash -xe\n");
        userData.append("INSTANCEID=$(curl -s -m 60 http://169.254.169.254/latest/meta-data/instance-id)\n");
        userData.append("aws --region eu-west-1 ec2 associate-address");
        userData.append(" --instance-id $INSTANCEID");
        userData.append(" --allocation-id ").append(eip);
        userData.append(" && ");
        userData.append("aws --region eu-west-1 ec2 modify-instance-attribute");
        userData.append(" --instance-id $INSTANCEID");
        userData.append(" --source-dest-check '{\"Value\": false}'");
        userData.append(" && ");
        userData.append("wget https://dl.influxdata.com/influxdb/releases/influxdb_1.6.4_amd64.deb\n");
        userData.append(" (aws --region eu-west-1 ec2 replace-route");
        userData.append(" --route-table-id ").append(routeTable);
        userData.append("--destination-cidr-block '0.0.0.0/0'");
        userData.append(" --instance-id $INSTANCEID");
        userData.append(" || ");
        userData.append("aws --region eu-west-1 ec2 create-route");
        userData.append(" --route-table-id ").append(routeTable);
        userData.append(" --destination-cidr-block '0.0.0.0/0'");
        userData.append(" --instance-id $INSTANCEID)");
        userData.append(" && ");
        userData.append("/opt/aws/bin/cfn-init -v --stack ").append(stackName).append(" --resource NATLaunchConfiguration --region eu-west-1\n");

        return new String(Base64.getEncoder().encode(userData.toString().getBytes()));
    }
}
