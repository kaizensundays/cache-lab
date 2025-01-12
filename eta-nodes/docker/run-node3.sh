#!/bin/bash

export SERVER_PORT=7701
export CONTAINER_SERVER_PORT=30731
export JGROUPS_RAFT_MEMBERS=A,B,C
export JGROUPS_RAFT_NODE_NAME=C

docker run -d --name eta-cache3 \
 -e SERVER_PORT=${SERVER_PORT} \
 -e JGROUPS_RAFT_MEMBERS=${JGROUPS_RAFT_MEMBERS} \
 -e JGROUPS_RAFT_NODE_NAME=${JGROUPS_RAFT_NODE_NAME} \
 -v /home/super/var/jgroups/.RAFT:/opt/.RAFT \
 -p ${CONTAINER_SERVER_PORT}:${SERVER_PORT} \
 localhost:32000/eta-cache:latest
