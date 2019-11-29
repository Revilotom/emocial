FROM alpine:3.10.1

RUN apk add --no-cache bash

RUN apk update \
    && apk --no-cache add openjdk11-jre-headless    \
    && rm -rf /var/cache/apk/*

RUN apk upgrade

ENV PROJECT_NAME play-java-jpa-example

ENV VERSION 1.0-SNAPSHOT

ENV SECRET QUStej67ubRe

ARG DBPASS

ENV DB_URL mysql://${DBPASS}@fpdb3.cxyf9g19a5as.ap-northeast-1.rds.amazonaws.com:3306/FP?useSSL=FALSE
# RUN echo "${DB_URL}"


RUN mkdir -p /usr/src/app

WORKDIR /usr/src/app

COPY ./${PROJECT_NAME}-${VERSION}.zip ./

RUN unzip ${PROJECT_NAME}-${VERSION}.zip

WORKDIR /usr/src/app/${PROJECT_NAME}-${VERSION}

RUN chmod a+x bin/${PROJECT_NAME}

CMD rm -rf RUNNING_PID && bin/${PROJECT_NAME} -Dapplication.secret=${SECRET} -Dconfig.resource=prod.conf -Dlogger.resource=prod-logger.xml

EXPOSE 9000 9000