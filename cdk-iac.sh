#!/bin/bash

usage() { echo "Usage: $0 [-c cdk] [-a App] [-d DTAP] [-p Platform (optional)]" 1>&2; exit 1; }

while getopts ":c:a:d:p:" o; do
    case "${o}" in
        c) c=${OPTARG};;
        a) a=${OPTARG};;
        d) d=${OPTARG};;
        p) p=${OPTARG};;
    esac
done
shift $((OPTIND-1))

if [[ -z ${c} ]] || [[ -z ${a} ]] || [[ -z ${d} ]]; then
    usage
fi

exec cdk --app "java -cp target/classes:$(cat .classpath.txt) io.haskins.cdkiac.application.${a} ${d} ${p}" ${c}