#!/bin/sh

kubectl delete -f cluster-autoscaler.yml
kubectl delete -f aws-alb-ingress-controller/
kubectl delete -f metrics-server/
kubectl delete -k ./
eksctl destroy cluster -f cluster.yml