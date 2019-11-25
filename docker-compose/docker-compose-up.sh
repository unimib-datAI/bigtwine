#!/bin/sh

docker-compose up -d mongodb kafka jhipster-registry
sleep 30
docker-compose up -d apigateway-app
sleep 30
docker-compose up -d