#!/bin/sh

eksctl create cluster -f ./cluster.yml --asg-access \
  && kubectl apply -f metrics-server/ \
  && ./kubectl-apply.sh \
  && kubectl config set-context --current --namespace=bigtwine \
  && kubectl create rolebinding admin --clusterrole=admin --user=system:serviceaccount:bigtwine:jobsupervisor --namespace=bigtwine \
  && eksctl create iamidentitymapping --name  bigtwine --role arn:aws:iam::535233662260:role/BigtwineCodeBuildKubectlRole --group system:masters --username codebuild

# Useful commands:
# kubectl annotate serviceaccount -n bigtwine default eks.amazonaws.com/role-arn=arn:aws:iam::535233662260:role/eksServiceRole
# eksctl utils associate-iam-oidc-provider --name=bigtwine --approve
# eksctl create iamserviceaccount --cluster=bigtwine --name=jobsupervisor --namespace=bigtwine --attach-policy-arn=arn:aws:iam::aws:policy/AmazonEKSServicePolicy
# eksctl create iamidentitymapping --name  bigtwine --role arn:aws:iam::535233662260:role/eksctl-bigtwine-addon-iamserviceaccount-bigt-Role1-14UI5IAHOASQ8 --group system:masters --username system:serviceaccount:bigtwine:jobsupervisor
# kubectl create rolebinding admin --clusterrole=admin --user=system:serviceaccount:bigtwine:jobsupervisor --namespace=bigtwine
