FROM navikt/java:17

COPY app/target/app.jar /app/app.jar
COPY export-vault-secrets.sh /init-scripts/50-export-vault-secrets.sh

ENV JAVA_OPTS="-Xmx1024m \
               --enable-preview \
               -Dspring.profiles.active=nais"
