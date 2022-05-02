#!/usr/bin/env bash

# delete purchases container
printf "\n==> Deleting Purchases API deployment and service\n"
kubectl delete -f purchases.yml

# delete kafka container
printf "\n==> Deleting Kafka deployment, service and persistent volume claim\n"
kubectl delete -f kafka.yml

# delete zookeeper container
printf "\n==> Deleting zookeeper deployment, service and persistent volume claim\n"
kubectl delete -f zookeeper.yml

# delete mysql container
printf "\n==> Deleting MySQL deployment, service and persistent volume claim\n"
kubectl delete -f mysql.yml

# delete secrets
printf "\n==> Deleting secrets\n"
kubectl delete -f secrets.yml

# delete namespace
printf "\n==> Deleting DEV namespace\n"
kubectl delete namespace tfm-dev-amartinm82