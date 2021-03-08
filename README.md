Tilbakemeldingsmottak
================

Tilbakemeldingsmottak / Serviceklage er en applikasjon med både frontend og backend
for behandling av serviceklager. 
Applikasjonen har skjema som sendes inn via [kontakt oss på nav.no](https://www.nav.no/person/kontakt-oss/nb/tilbakemeldinger/serviceklage/login)  
Informasjon sendes inn via en kontakt-oss-api app, Serviceklage omgjør da skjemaet til en pdf,  
oppretter en kontroll oppgave som følges opp av ansvarlig enhet.

# Komme i gang

Hvordan bygge, teste og kjøre koden

## Bygging lokalt
* Applikasjonen benytter java 11, dette må være installert
* toolchain.xml må settes opp i ./m2/toolchain.xml
* applikasjonen bygger med mvn clean install
* avhengigheter til [Internt repo](https://repo.adeo.no)

## Kjøring lokalt
### For å kjøre applikasjonen lokalt med npm@6.x.x installert:
* cd web\src\main\web_src
* npm install
* npm start
* Kan gå til [http://localhost:3000/serviceklage/klassifiser](http://localhost:3000/serviceklage/klassifiser)
### For kjøring av applikasjonen med npm*7.x.x
* cd web\src\main\web_src
* npm install --save --legacy-peer-deps
* npm start
* Kan gå til [http://localhost:3000/serviceklage/klassifiser](http://localhost:3000/serviceklage/klassifiser)

## Bygg til akseptansetestmiljø
* Applikasjonen er PT satt opp for bygg mot Q1
* [Byggserver](https://dok-jenkins.adeo.no/job/tilbakemeldingsmottak2/) må åpnes i *Chrome skss*  
* Jobben krever at en godkjenner  
    * Build and push dockerimage
    * deploy?
### Test i miljøet
For å teste appliaksjonen er en avhengig av å ha:
1. en test-ident for saksbehandler opprettet i [Ida](https://ida.nais.adeo.no/)
   - brukeren må ha nasjonal tilgang, tilgang til gosys, enhet 4200 og tema Serviceklage
2. testperson opprettet i [dolly](https://dolly.nais.preprod.local/)

Innsending gjøres via 
    [Ditt nav](http://www.dev.nav.no/person/dittnav) eller innlogget/uinlogget fra [Kontakt oss/Tilbakemelding](https://www.dev.nav.no/person/kontakt-oss/nb/tilbakemeldinger) 

Saksbehandling gjøres via [Gosys](https://gosys-nais-q1.nais.preprod.local/gosys/)
- logg på med test-saksbehandler
*When it fails*
* under bygg av docker image eller deploy må det gjennomføres ny commit i branch.  
Dette må gjøres fordi det må genereres nytt dockerimage da disse må være unike

Kjente feil | Løsning  
----------- | -------  
Docker image feiler | løs feil fra loggene,  oppdatter pr
Bygget feiler | Sjekk loggene, oppdatter pr  



## Deploy til produksjon
* Ved merge til master trigges bygg på [master branch](https://dok-jenkins.adeo.no/job/tilbakemeldingsmottak2/job/master/)

 Steg: | Build cangeset | Relase | SCM tag | Sonar rapport | Build release | Deploy til q1 | Produksjon? 
------- |  ---------- | ---------- | ---------- | ---------- | ---------- | ---------- |---------- |
 Handling:| Automatisk | Velg versjons bump | Automatisk | Automatisk | Automatisk | Automatisk | Se under 

1. Hvis ja, bekreft at den skal rett ut
2. Hvis nei, velg **abort**, Deploy til prod av denne versjonen gjørse da gjennom egen jobb  [Deply Naiserator](https://dok-jenkins.adeo.no/view/deploy/job/deploy-naiserator/)

---

# Henvendelser

Enten:
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

Eller:
Spørsmål knyttet til kode eller prosjektet kan rettes mot:
[mail to team-soknad@nav.no](mailto:team-innsending@nav.no)

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen **#Serviceklage**.
