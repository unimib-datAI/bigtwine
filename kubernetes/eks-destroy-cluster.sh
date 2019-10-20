#!/bin/bash

kubectl delete -k ./
kubectl delete -f cluster-autoscaler.yml
kubectl delete -f aws-alb-ingress-controller/
kubectl delete -f metrics-server/
echo "Manually deleting security groups created by ALB"
for sg in $(aws ec2 describe-security-groups --filter "Name=tag:alpha.eksctl.io/cluster-name,Values=bigtwine" --query "SecurityGroups[*].GroupId" --output text)
do
  echo "Deling security group: $sg"
  aws ec2 delete-security-group --group-id $sg
done
eksctl delete cluster -f cluster.yml