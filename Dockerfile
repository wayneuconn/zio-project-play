FROM --platform=linux/amd64 openjdk:17-alpine
RUN apk add --no-cache \
    bash \
    wget

ENV SECRETS_INIT_VERSION=v0.2.11
ENV SECRETS_INIT_URL=https://github.com/doitintl/secrets-init/releases/download/$SECRETS_INIT_VERSION/secrets-init_Linux_amd64.tar.gz
ENV SECRETS_INIT_SHA256=37d896d28b61523acf5e58e2e3925733e6796f18920df85be0b8b1a973e24604
RUN mkdir -p /opt/secrets-init && cd /opt/secrets-init \
    && wget -qO secrets-init.tar.gz "$SECRETS_INIT_URL" \
    && echo "$SECRETS_INIT_SHA256  secrets-init.tar.gz" | sha256sum -c - \
    && tar -xzvf secrets-init.tar.gz \
    && mv secrets-init /usr/local/bin \
    && rm secrets-init.tar.gz

EXPOSE 9001

RUN mkdir /app

COPY build/libs/*-all.jar /app/server.jar
CMD ["/usr/local/bin/secrets-init", "--provider=google", "java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseContainerSupport", "-Dlog4j2.formatMsgNoLookups=true", "-Djava.security.egd=file:/dev/./urandom", "-cp", "com.dv01.ZIOHttpApp", "-jar" , "/app/server.jar"]
