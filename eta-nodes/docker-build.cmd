set JAVA_HOME="%JAVA_17_HOME%"

copy bin\service.jar docker

mvn clean install docker:build -P docker

