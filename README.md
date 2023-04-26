Tilbakemeldingsmottak
================

Tilbakemeldingsmottak / Serviceklage er en applikasjon med både frontend og backend
for mottak og behandling av serviceklager. 
Applikasjonen har skjema som sendes inn via [kontakt oss på nav.no](https://www.nav.no/person/kontakt-oss/nb/tilbakemeldinger/serviceklage/login)  
Informasjon sendes inn via en kontakt-oss-api app, Serviceklage omgjør da skjemaet til en pdf,  
oppretter en kontroll oppgave som følges opp av ansvarlig enhet.

# Komme i gang

Applikasjonen kjører Java 17. Hvordan bygge, teste og kjøre koden:

## Bygging lokalt
* Kjør `mvn clean install`
* Kjør `docker-compose up` for å kjøre opp mocks og database lokalt
* Kjør Spring Boot applikasjonen i IntelliJ

### Autentisering

Denne applikasjonen autentiseres med issuers `azuread` og `tokenx`. En mock auth server kjøres via docker-compose og kan brukes til å generere gyldige tokens lokalt. 
TokenX brukes til å skille mellom innlogget og uinlogget innsending av serviceklager. Vi skiller også på Azure AD brukere som skal klassifisere serviceklager og brukere som skal hente ut data til datavarehuset. 

- For tokenx (brukere som er innlogget og sender inn serviceklage):
    - Gå til `http://localhost:6969/tokenx/debugger` og velg "Get a token" med hva som helst i user objektet. Et `pid` claim er lagt på i tokenet
- For azuread (vanlig `client_credentials`, ikke-innlogget server-til-server kommunikasjon):
  - Gå til `http://localhost:6969/azuread/debugger` og velg "Get a token" med hva som helst i user objektet
- For azuread (brukere som skal klassifisere serviceklager):
  - Gå til `http://localhost:6969/azuread/debugger` og bytt ut `somescope` med `frontend`. Velg deretter "Get a token" med hva som helst i user objektet
- For azuread (brukere som skal hente ut data til datavarehuset):
    - Gå til `http://localhost:6969/azuread/debugger` og bytt ut `somescope` med `datavarehus`. Velg deretter "Get a token" med hva som helst i user objektet


## Bygg og deploy til akseptansetestmiljø
* Applikasjonen er satt opp for bygg mot `dev-gcp`
* Merge din branch inn i [preprod-pipeline](https://github.com/navikt/tilbakemeldingsmottak-api/tree/preprod-pipeline)
  * Da blir appliakskjonen deployet og du kan se status [her](https://github.com/navikt/tilbakemeldingsmottak-api/actions)
    * Kun merge til prod krever PR

### Test i miljøet
For å teste appliaksjonen er en avhengig av å ha:
1. En test-ident for saksbehandler opprettet i [Ida](https://ida.nais.adeo.no/)
   - Brukeren må ha nasjonal tilgang, tilgang til gosys, enhet 4200 og tema SRV Serviceklage
2. Testperson opprettet i [dolly](https://dolly.nais.preprod.local/)

Innsending gjøres via 
    [Ditt nav](http://www.dev.nav.no/person/dittnav) eller innlogget/uinlogget fra [Kontakt oss/Tilbakemelding](https://www.dev.nav.no/person/kontakt-oss/nb/tilbakemeldinger) 

Saksbehandling gjøres via [Gosys](https://gosys-nais-q1.nais.preprod.local/gosys/)
- Logg på med test-saksbehandler
*When it fails*
* Under bygg av docker image eller deploy må det gjennomføres ny commit i branch.  
Dette må gjøres fordi det må genereres nytt dockerimage da disse må være unike

Kjente feil | Løsning  
----------- | -------  
Docker image feiler | løs feil fra loggene,  oppdatter pr
Bygget feiler | Sjekk loggene, oppdatter pr  

## Regenerering av serviceklage PDF 
Dersom det har gått feil i generering av PDF av serviceklage som har blitt lagt inn i arkivet, kan denne regenereres ved å benytte testen i ReGenereringAvPdf. Dette forutsetter at det bygges opp en xslx fil med data basert på serviceklagen i databasen og bruker informasjon og datoer i fra arkivet.

## Big Query
Det legges inn data for serviceklager i Big Query (`tilbakemeldingsmottak_metrics`) for at datavarehus skal kunne hente ut statistikk på dette.

## Deploy til produksjon
Håndteres av [Github workflow](https://github.com/navikt/tilbakemeldingsmottak-api/tree/main/.github/workflows)

## OpenAPI
En OpenAPI definisjon ligger i [api/src/main/resources/tilbakemeldingsmottak-api.yml](api/src/main/resources/tilbakemeldingsmottak-api.yml) 
og brukes til å generere interfaces og domeneobjekter for apiet, samt som dokumentasjon på apiet
(dette gjøres via [OpenAPI Generator Maven Plugin](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin)).
Den kan også enkelt importeres i Postman eller andre verktøy for å teste apiet. 

For å opprettholde samme format på enums som ble brukt før, er det lagt til egne templates for dette. 
Disse er basert på [templates](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator/src/main/resources/JavaSpring) fra OpenAPI Generator. 

# Henvendelser
Applikasjonen vedlikeholdes av teamserviceklage / Team søknad som er ansvarlig for 4 tjenestser.

Enten:
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

Eller:
Spørsmål knyttet til kode eller prosjektet kan rettes mot:
[mail to team-soknad@nav.no](mailto:team-innsending@nav.no)

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen **#Serviceklage**.
