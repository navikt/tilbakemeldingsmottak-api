Tilbakemeldingsmottak
================

Tilbakemeldingsmottak / Serviceklage er en backend applikasjon for mottak og behandling av serviceklager, samt ris/ros.
Applikasjonen har skjema som sendes inn via [tilbakemeldinger](https://www.nav.no/person/kontakt-oss/nb/tilbakemeldinger/)  
Serviceklager omgjøres til en pdf og det opprettes en kontrolloppgave som følges opp av ansvarlig enhet. Ris/ros sendes videre på mail til den ansvarlige.

# Komme i gang

Applikasjonen kjører Java 17. Hvordan bygge, teste og kjøre koden:

## Bygging lokalt

* Kjør `mvn clean install`
* Kjør `docker-compose up` for å kjøre opp mocks og database lokalt
* Kjør Spring Boot applikasjonen med `local` som aktiv profil (`-Dspring.profiles.active=local`)

### Autentisering

Denne applikasjonen autentiseres med issuers `azuread` og `tokenx`. En mock auth server kjøres via docker-compose og kan
brukes til å generere gyldige tokens lokalt.
TokenX brukes til å skille mellom innlogget og uinlogget innsending av serviceklager.

- For tokenx (brukere som er innlogget og sender inn serviceklage):
    - Gå til `http://localhost:6969/tokenx/debugger` og velg "Get a token" med hva som helst i user objektet. Et `pid`
      claim er lagt på i tokenet
- For azuread (vanlig `client_credentials`, ikke-innlogget server-til-server kommunikasjon):
    - Gå til `http://localhost:6969/azuread/debugger` og velg "Get a token" med hva som helst i user objektet
- For azuread (brukere som skal klassifisere serviceklager):
    - Gå til `http://localhost:6969/azuread/debugger` og bytt ut `somescope` med `frontend`. Velg deretter "Get a token"
      med hva som helst i user objektet

### Test i miljøet

For å teste appliaksjonen er en avhengig av å ha:

1. En test-ident for saksbehandler opprettet i [Ida](https://ida.nais.adeo.no/)
    - Brukeren må ha nasjonal tilgang, tilgang til gosys, enhet 4200 og tema SRV Serviceklage
2. Testperson opprettet i [dolly](https://dolly.nais.preprod.local/)

Innsending gjøres innlogget/uinlogget fra [Kontakt oss/Tilbakemelding](https://www.intern.dev.nav.no/person/kontakt-oss/nb/tilbakemeldinger)

Saksbehandling gjøres via [Gosys](https://gosys-nais-q1.nais.preprod.local/gosys/) 

## Regenerering av serviceklage PDF

Dersom det har gått feil i generering av PDF av serviceklage som har blitt lagt inn i arkivet, kan denne regenereres ved
å benytte testen i ReGenereringAvPdf. Dette forutsetter at det bygges opp en xslx fil med data basert på serviceklagen i
databasen og bruker informasjon og datoer i fra arkivet.

## Big Query og datavarehus

Det legges inn data for serviceklager i Big Query (`tilbakemeldingsmottak_metrics`) for at datavarehus skal kunne hente
ut statistikk på dette.

## Deploy til dev/prod

Håndteres av [Github workflow](https://github.com/navikt/tilbakemeldingsmottak-api/tree/main/.github/workflows)

## OpenAPI

En OpenAPI definisjon ligger
i [api/src/main/resources/tilbakemeldingsmottak-api.yml](api/src/main/resources/tilbakemeldingsmottak-api.yml)
og brukes til å generere interfaces og domeneobjekter for apiet, samt som dokumentasjon på apiet
(dette gjøres
via [OpenAPI Generator Maven Plugin](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin)).
Den kan også enkelt importeres i Postman eller andre verktøy for å teste apiet.

For å opprettholde samme format på enums som ble brukt før, er det lagt til egne templates for dette.
Disse er basert
på [templates](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator/src/main/resources/JavaSpring)
fra OpenAPI Generator.

# Henvendelser

Applikasjonen vedlikeholdes av teamserviceklage / Team Fyllut Sendinn.

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen **#team-fyllut-sendinn**.
