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
* Applikasjonen benytter java 8, dette må være installert
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

## Bygg til miljø
* Applikasjonen er PT satt opp for bygg mot Q1
* [Byggserver](https://dok-jenkins.adeo.no/job/tilbakemeldingsmottak2/) må åpnes i *Chrome skss*  
* Jobben krever at en godkjenner  
    * Build and push dockerimage
    * deploy?
    
*When it fails*
* under bygg av docker image eller deploy må det gjennomføres ny commit i branch.  
Dette må gjøres fordi det må genereres nytt dockerimage da disse må være unike

Kjente feil | Løsning  
----------- | -------  
Docker image feiler | løs feil fra loggene,  oppdatter pr
Bygget feiler | Sjekk loggene, oppdatter pr  



## Deploy til produksjon
* utgangspunkt fra master
- [ ] TODO å skrive når det blir gjort.

---

# Henvendelser

Enten:
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub

Eller:
Spørsmål knyttet til kode eller prosjektet kan rettes mot:
[mail to team-soknad@nav.no](mailto:team-soknad@nav.no)

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #Serviceklage.
