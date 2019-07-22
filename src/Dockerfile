FROM openjdk:8-jdk-alpine

RUN  mkdir /identity-repos
COPY scripts/run.sh /run.sh
COPY repo-search-string /repo-search-string
COPY indexes/is460 /is460
COPY indexes/is500 /is500
COPY indexes/is510 /is510
COPY indexes/is520 /is520
COPY indexes/is530 /is530
COPY indexes/is540 /is540
COPY indexes/is541 /is541
COPY indexes/is550 /is550
COPY indexes/is560 /is560
COPY indexes/is570 /is570
COPY indexes/is580 /is580

COPY org.facilelogin.wso2is.repo.explorer/target/org.facilelogin.wso2is.repo.explorer-1.0.0.jar /org.facilelogin.wso2is.repo.explorer-1.0.0.jar

RUN apk add --update bash && rm -rf /var/cache/apk/*

RUN apk update \
    && apk add wget \
    && rm -rf /var/cache/apk/*

RUN apk update \
    && apk add tree \
    && rm -rf /var/cache/apk/*

RUN apk update \
    && apk add sed \
    && rm -rf /var/cache/apk/*

RUN apk update \
    && apk add git \
    && rm -rf /var/cache/apk/*

RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*

RUN apk update \
    && apk add jq \
    && rm -rf /var/cache/apk/*

ENTRYPOINT ["bash" , "run.sh"]
