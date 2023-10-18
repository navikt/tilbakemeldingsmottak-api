package no.nav.tilbakemeldingsmottak.bigquery.serviceklager

enum class ServiceklageEventType {
    // Ved opprettelse av serviceklage
    OPPRETT_SERVICEKLAGE,

    // Ved klassifisering av serviceklage
    KLASSIFISER_SERVICEKLAGE,

    // Ved oppdatering av serviceklage (feks ved feilregistrering eller ved manglende felt)
    OPPDATER_SERVICEKLAGE,
}
