#!/bin/sh

PACKAGE_VERSION=$(cat ../../package.json \
  | grep version \
  | head -1 \
  | awk -F: '{ print $2 }' \
  | sed 's/[",]//g' \
  | tr -d '[[:space:]]')

docker build -t bigtwine/frontend . \
    && docker tag bigtwine/frontend 535233662260.dkr.ecr.eu-central-1.amazonaws.com/bigtwine/frontend \
    && docker tag bigtwine/frontend 535233662260.dkr.ecr.eu-central-1.amazonaws.com/bigtwine/frontend:$PACKAGE_VERSION \
    && docker push 535233662260.dkr.ecr.eu-central-1.amazonaws.com/bigtwine/frontend \
    && docker push 535233662260.dkr.ecr.eu-central-1.amazonaws.com/bigtwine/frontend:$PACKAGE_VERSION
