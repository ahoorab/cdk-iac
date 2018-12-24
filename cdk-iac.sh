#!/bin/bash

usage() {
    echo "Usage: $0 [Options] <cdk-command> <template> <application> <dtap> "
    echo "Options:";
    echo "  -v  VPC";
    echo "  -p  Use DTAP as profile";
    echo "";
    exit 1;
}

while getopts ":v:p" o; do
    case "${o}" in
        v) v=${OPTARG};;
        p) profiles=1;;
    esac
done
shift $((OPTIND-1))

if [[ -z ${1} ]] || [[ -z ${2} ]] || [[ -z ${3} ]] || [[ -z ${4} ]]; then
    usage
fi

CDK_COMMAND="$1"
TEMPLATE="io.haskins.cdkiac.template.$2"
APPLICATION="-Dapplication=$3"
DTAP="-Ddtap=$4 "

JAVA_COMMAND="java -cp"
JAVA_COMMAND_FULL="java -cp target/classes:$(cat .classpath.txt)"

VPC=""
if [[ -n ${v} ]]; then
    VPC="-Dvpc=${v} " # space at the end needed
fi

PROFILE=""
if [[ ${profiles} == 1 ]]; then
    PROFILE="--profile $4"
fi

echo "Running command : cdk ${PROFILE} --app \"${JAVA_COMMAND} ${APPLICATION} ${DTAP}${VPC}${TEMPLATE}\" ${CDK_COMMAND}"
exec cdk ${PROFILE} --app "${JAVA_COMMAND_FULL} ${APPLICATION} ${DTAP}${VPC}${TEMPLATE}" ${CDK_COMMAND}
