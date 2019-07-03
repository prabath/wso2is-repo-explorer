FROM alpine

RUN  mkdir /identity-repos
COPY wso2is-git-introspection.sh /wso2is-git-introspection.sh

RUN apk add --update bash && rm -rf /var/cache/apk/*

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

ENTRYPOINT ["bash" , "wso2is-git-introspection.sh"]
