#!/bin/bash

export FLAGS="-server -Xmx256m"
export FLAGS="$FLAGS -XX:CompileThreshold=10000 -XX:ThreadStackSize=64K -XX:SurvivorRatio=8"
export FLAGS="$FLAGS -XX:TargetSurvivorRatio=90 -XX:MaxTenuringThreshold=15"
export FLAGS="$FLAGS -Xshare:off"

export JG_FLAGS="-Djgroups.udp.mcast_addr=232.5.5.5 -Djava.net.preferIPv4Stack=true"

java $FLAGS $JG_FLAGS \
 -Dlog4j2.configurationFile=log4j2.xml -Dlog4j.shutdownHookEnabled=false -Dlog4j2.debug=false \
 -Dproperties=node.yaml \
 -Dcom.sun.management.jmxremote.port=7901 \
 -Dcom.sun.management.jmxremote.rmi.port=7901 \
 -Dcom.sun.management.jmxremote.authenticate=false \
 -Dcom.sun.management.jmxremote.ssl=false \
 -Dcom.sun.management.jmxremote.host=0.0.0.0 \
 -Djava.rmi.server.hostname=0.0.0.0 \
 -Dloader.main=com.kaizensundays.eta.cache.Main \
 -cp service.jar org.springframework.boot.loader.launch.PropertiesLauncher
