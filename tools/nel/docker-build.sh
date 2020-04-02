#!/bin/sh

LOCAL_IMAGE_NAME=bigtwine-nel-tool
REPOSITORY_URI=bigtwine/nel-tool
COMMIT_HASH=$(git rev-parse HEAD | cut -c 1-7)
IMAGE_TAG=${COMMIT_HASH:=latest}

docker build -t $LOCAL_IMAGE_NAME:latest .
docker tag $LOCAL_IMAGE_NAME:latest $REPOSITORY_URI:latest
docker tag $LOCAL_IMAGE_NAME:latest $REPOSITORY_URI:$IMAGE_TAG

docker push $REPOSITORY_URI:latest
docker push $REPOSITORY_URI:$IMAGE_TAG
