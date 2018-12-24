#!/bin/bash

usage() {
    echo "Usage: $0"
    echo "Options:";
    echo "    -c cdk          REQUIRED: CDK command to run";
    echo "    -t cdk          REQUIRED: Template to use";
    echo "    -a cdk          REQUIRED: Application Name";
    echo "    -d cdk          REQUIRED: DTAP";
    echo "    -v cdk          VPC to use";
    echo "    -p cdk          Use DTAP as profile";
    exit 1;
}

while getopts "c:t:a:d:v:p" o; do
    case "${o}" in
        c) c=${OPTARG};;
        t) t=${OPTARG};;
        a) a=${OPTARG};;
        d) d=${OPTARG};;
        v) v=${OPTARG};;
        p) profiles=1;;
    esac
done
shift $((OPTIND-1))

if [[ -z ${c} ]] || [[ -z ${t} ]] || [[ -z ${a} ]] || [[ -z ${d} ]]; then
    usage
fi

JAVA_COMMAND="java -cp"
JAVA_COMMAND_FULL="java -cp target/classes:$(cat .classpath.txt)"

CDK_COMMAND="${c}"
TEMPLATE="io.haskins.cdkiac.template.${t}"
APPLICATION="-Dapplication=${a} " # space at the end needed
DTAP="-Ddtap=${d} " # space at the end needed

VPC=""
if [[ -n ${v} ]]; then
    VPC="-Dvpc=${v} " # space at the end needed
fi

PROFILE=""
if [[ ${profiles} == 1 ]]; then
    PROFILE="--profile ${d}"
fi

echo "Running command : cdk ${PROFILE} --app \"${JAVA_COMMAND} ${APPLICATION}${DTAP}${VPC}${TEMPLATE}\" ${c}"
exec cdk ${PROFILE} --app "${JAVA_COMMAND_FULL} ${APPLICATION}${DTAP}${VPC}${TEMPLATE}" ${c}
