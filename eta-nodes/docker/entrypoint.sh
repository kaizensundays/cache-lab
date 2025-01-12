#!/bin/bash

export FLAGS="-server -Xmx256m"
export FLAGS="$FLAGS -XX:CompileThreshold=10000 -XX:ThreadStackSize=64K -XX:SurvivorRatio=8"
export FLAGS="$FLAGS -XX:TargetSurvivorRatio=90 -XX:MaxTenuringThreshold=15"
export FLAGS="$FLAGS -Xshare:off"

export JG_FLAGS="-Djgroups.udp.mcast_addr=232.5.5.5 -Djava.net.preferIPv4Stack=true"

java $FLAGS $JG_FLAGS \
 -Dlog4j2.configurationFile=log4j2.xml -Dlog4j.shutdownHookEnabled=false -Dlog4j2.debug=false \
 -Dproperties=node.yaml \
 -Dloader.main=com.kaizensundays.eta.jgroups.Main \
 -cp service.jar org.springframework.boot.loader.launch.PropertiesLauncher
