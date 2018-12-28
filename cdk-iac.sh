#!/bin/bash

usage() {
    echo "Usage: $0 [Options] <cdk-command> <template> <application> <dtap>"
    echo "Options:";
    echo "  -v  VPC";
    echo "  -p  Use DTAP as profile";
    echo "";
    exit 1;
}

CMD_OPTIONS=""

while getopts ":v:-:p-" o; do
    case "${o}" in
        v) v=${OPTARG};;
        -)
           case "${OPTARG}" in
               profile) profiles=1;;
               trace) CMD_OPTIONS+=" --trace";;
               strict) CMD_OPTIONS+=" --strict";;
               ignore-errors) CMD_OPTIONS+=" --ignore-errors";;
               json) CMD_OPTIONS+=" --json";;
               output) CMD_OPTIONS+=" --output ${!OPTIND}"; OPTIND=$(( $OPTIND + 1 ));;
           esac;;
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

if [[ ${profiles} == 1 ]]; then
    CMD_OPTIONS+=" --profile $4"
fi

echo "Running command : cdk ${CMD_OPTIONS} --app \"${JAVA_COMMAND} ${APPLICATION} ${DTAP}${VPC}${TEMPLATE}\" ${CDK_COMMAND}"
exec cdk ${CMD_OPTIONS} --app "${JAVA_COMMAND_FULL} ${APPLICATION} ${DTAP}${VPC}${TEMPLATE}" ${CDK_COMMAND}
