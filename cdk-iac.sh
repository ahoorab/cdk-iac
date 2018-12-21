#!/bin/bash

usage() { echo "Usage: $0 [-c cdk] [-t template] [-a application] [-d DTAP] [-p Platform (optional)]" 1>&2; exit 1; }

while getopts ":c:t:a:d:p:" o; do
    case "${o}" in
        c) c=${OPTARG};;
        t) t=${OPTARG};;
        a) a=${OPTARG};;
        d) d=${OPTARG};;
        p) p=${OPTARG};;
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
PLATFORM="-Dplatform=${p}"


# exec cdk --profile ${d} --app "java -cp target/classes:$(cat .classpath.txt) io.haskins.cdkiac.template.${t} ${a} ${d} ${p}" ${c}
exec cdk --app "${JAVA_COMMAND} ${APPLICATION} ${DTAP} ${PLATFORM} ${TEMPLATE}" ${c}
