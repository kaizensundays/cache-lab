#!/bin/bash

export JAVA_HOME=$JAVA_17_HOME

export LOG=-Dlog4j.configurationFile=log4j2.xml

export MCAST_ADDR=232.5.5.5

export JG_FLAGS=-Djgroups.udp.mcast_addr=$MCAST_ADDR
export JG_FLAGS="$JG_FLAGS -Djava.net.preferIPv4Stack=true"

export FLAGS="-server -Xmx1G"
export FLAGS="$FLAGS -XX:CompileThreshold=10000 -XX:ThreadStackSize=64K -XX:SurvivorRatio=8"
export FLAGS="$FLAGS -XX:TargetSurvivorRatio=90 -XX:MaxTenuringThreshold=15"
export FLAGS="$FLAGS -Xshare:off"

export SERVER_PORT=7701
export JGROUPS_RAFT_MEMBERS=A,B,C
export JGROUPS_RAFT_NODE_NAME=A

export CMD="$JAVA_HOME/bin/java $FLAGS $JG_FLAGS $LOG -Dproperties=node.yaml -Dlogging.config=log4j2-1.xml -Dloader.main=com.kaizensundays.eta.jgroups.Main -cp service.jar org.springframework.boot.loader.launch.PropertiesLauncher"

gnome-terminal -- bash -c "$CMD"

