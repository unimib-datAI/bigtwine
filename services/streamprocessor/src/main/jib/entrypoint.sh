#!/bin/sh

echo $0
echo $1
echo "${@:2}"
exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "$@"
