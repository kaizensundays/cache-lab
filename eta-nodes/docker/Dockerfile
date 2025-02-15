FROM bellsoft/liberica-openjdk-alpine:17

RUN apk update; \
    apk add vim; \
    apk add bash; \
    apk add htop; \
    apk add unzip;

RUN echo -e "alias hh='history'\n alias ll='ls -alF'" > /root/.bashrc

WORKDIR /opt

COPY log4j2.xml .
COPY node.yaml .
COPY entrypoint.sh .
COPY service.jar .

RUN dos2unix *.sh; chmod ug+x *.sh

EXPOSE 7701
EXPOSE 7901

ENTRYPOINT ["./entrypoint.sh"]
