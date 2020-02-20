#!/usr/bin/env sh

if test -f /var/run/secrets/nais.io/srvtilbakemeldingsmottak/username;
then
    echo "Setting SERVICEUSER_USERNAME"
    export SERVICEUSER_USERNAME=$(cat /var/run/secrets/nais.io/srvtilbakemeldingsmottak/username)
fi

if test -f /var/run/secrets/nais.io/srvtilbakemeldingsmottak/password;
then
    echo "Setting SERVICEUSER_PASSWORD"
    export SERVICEUSER_PASSWORD=$(cat /var/run/secrets/nais.io/srvtilbakemeldingsmottak/password)
fi

if test -f /var/run/secrets/nais.io/tilbakemeldingsmottak/username;
then
    echo "Setting SPRING_DATASOURCE_USERNAME"
    export SERVICEUSER_USERNAME=$(cat /var/run/secrets/nais.io/tilbakemeldingsmottak/username)
fi

if test -f /var/run/secrets/nais.io/tilbakemeldingsmottak/password;
then
    echo "Setting SPRING_DATASOURCE_PASSWORD"
    export SERVICEUSER_PASSWORD=$(cat /var/run/secrets/nais.io/tilbakemeldingsmottak/password)
fi