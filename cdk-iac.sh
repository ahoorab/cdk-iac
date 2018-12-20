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

exec cdk --app "java -cp target/classes:$(cat .classpath.txt) io.haskins.cdkiac.template.${t} ${a} ${d} ${p}" ${c}
