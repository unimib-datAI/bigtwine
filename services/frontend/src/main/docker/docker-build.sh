#!/bin/sh

PACKAGE_VERSION=$(cat ../../package.json \
  | grep version \
  | head -1 \
  | awk -F: '{ print $2 }' \
  | sed 's/[",]//g' \
  | tr -d '[[:space:]]')

docker build -t bigtwine/frontend . \
    && docker tag bigtwine/frontend bigtwine/frontend:$PACKAGE_VERSION \
    && docker push bigtwine/frontend \
    && docker push bigtwine/frontend:$PACKAGE_VERSION
