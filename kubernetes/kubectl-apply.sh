#!/bin/bash
# Files are ordered in proper order with needed wait for the dependent custom resource definitions to get initialized.
# Usage: bash kubectl-apply.sh

kubectl apply -f namespace.yml
kubectl apply -f messagebroker/
kubectl apply -f db/
kubectl apply -f registry/
kubectl apply -f analysis/
kubectl apply -f apigateway/
kubectl apply -f cronscheduler/
kubectl apply -f geo/
kubectl apply -f jobsupervisor/
kubectl apply -f linkresolver/
kubectl apply -f nel/
kubectl apply -f ner/
kubectl apply -f socials/
kubectl apply -f console/
kubectl apply -f frontend/
