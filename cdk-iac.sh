#!/bin/bash

usage() { echo "Usage: $0 [-c cdk] [-t template] [-a application] [-d DTAP] [-v VPC (optional)]" 1>&2; exit 1; }

while getopts ":c:t:a:d:v:" o; do
    case "${o}" in
        c) c=${OPTARG};;
        t) t=${OPTARG};;
        a) a=${OPTARG};;
        d) d=${OPTARG};;
        v) v=${OPTARG};;
    esac
done
shift $((OPTIND-1))

if [[ -z ${c} ]] || [[ -z ${t} ]] || [[ -z ${a} ]] || [[ -z ${d} ]]; then
    usage
fi

JAVA_COMMAND="java -cp target/classes:$(cat .classpath.txt)"

CDK_COMMAND="${c}"
TEMPLATE="io.haskins.cdkiac.template.${t}"
APPLICATION="-Dapplication=${a}"
DTAP="-Ddtap=${d}"
VPC="-Dvpc=${v}"


# exec cdk --profile ${d} --app "java -cp target/classes:$(cat .classpath.txt) io.haskins.cdkiac.template.${t} ${a} ${d} ${v}" ${c}
exec cdk --app "${JAVA_COMMAND} ${APPLICATION} ${DTAP} ${VPC} ${TEMPLATE}" ${c}
