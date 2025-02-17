@echo off

set JAVA_HOME=%JAVA_17_HOME%

set LOG=-Dlog4j.configurationFile=log4j2.xml

set MCAST_ADDR=232.5.5.5

set JG_FLAGS=-Djgroups.udp.mcast_addr=%MCAST_ADDR%
set JG_FLAGS=%JG_FLAGS% -Djava.net.preferIPv4Stack=true

set FLAGS=-server -Xmx1G
set FLAGS=%FLAGS% -XX:CompileThreshold=10000 -XX:ThreadStackSize=64K -XX:SurvivorRatio=8
set FLAGS=%FLAGS% -XX:TargetSurvivorRatio=90 -XX:MaxTenuringThreshold=15
set FLAGS=%FLAGS% -Xshare:off

set SERVER_PORT=7703
set JGROUPS_RAFT_MEMBERS=A,B,C
set JGROUPS_RAFT_NODE_NAME=C

start "Node" %JAVA_HOME%/bin/java %FLAGS% %JG_FLAGS% %LOG% ^
		-Dproperties=node.yaml ^
		-Dlogging.config=log4j2-3.xml ^
                -Dloader.main=com.kaizensundays.eta.cache.Main ^
           	-cp service.jar org.springframework.boot.loader.launch.PropertiesLauncher
