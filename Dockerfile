FROM navikt/java:17

COPY app/target/app.jar /app/app.jar
COPY export-vault-secrets.sh /init-scripts/50-export-vault-secrets.sh
COPY tlssetting.properties /securityoverride/tlssetting.properties

ENV JAVA_OPTS="-Xmx1024m \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=nais \
               -Djava.security.properties=/securityoverride/tlssetting.properties \
               -Dhttp.proxyHost=webproxy.nais \
               -Dhttps.proxyHost=webproxy.nais \
               -Dhttp.proxyPort=8088 \
               -Dhttps.proxyPort=8088 \
               -Dhttp.nonProxyHosts=*.local"