FROM ghcr.io/navikt/baseimages/temurin:17

COPY app/target/app.jar /app/app.jar

ENV JAVA_OPTS="-Xmx1024m \
               --enable-preview \
               -Dspring.profiles.active=nais"
EXPOSE 9069
