#!/usr/bin/env bash

# Create namespace
printf "\n==> Creating DEV namespace\n"
kubectl create namespace tfm-dev-amartinm82

# create secrets
printf "\n==> Creating secrets\n"
kubectl apply -f secrets.yml

# start mysql container
printf "\n==> Starting MySQL deployment, service and persistent volume claim\n"
kubectl apply -f mysql.yml

# start zookeeper container
printf "\n==> Starting Zookeeper deployment, service and persistent volume claim\n"
kubectl apply -f zookeeper.yml

# start kafka container
printf "\n==> Starting Kafka deployment, service and persistent volume claim\n"
kubectl apply -f kafka.yml

# start api Purchases container
printf "\n==> Starting Purchases deployment and service\n"
kubectl apply -f purchases.yml