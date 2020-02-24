#!/bin/bash

__realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}

SERVICES="ner nel"
REPO_URL_TEMPLATE="https://github.com/UNIMIBInside/bigtwine-{{SERVICE_NAME}}-tool.git"
SCRIPT_PATH=$(__realpath "$0")
SERVICES_PATH=$(dirname "$SCRIPT_PATH")

for service in $SERVICES
do
  REPO_URL=$(echo $REPO_URL_TEMPLATE | sed -e s/{{SERVICE_NAME}}/$service/)
  echo "Cloning tool ${service} from remote ${REPO_URL}"
  git clone $REPO_URL "${SERVICES_PATH}/${service}"
done
