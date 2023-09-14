package no.nav.tilbakemeldingsmottak.bigquery.serviceklager

enum class ServiceklageEventType {
    // Ved oppdatering av serviceklage (feks ved feilregistrering eller ved manglende felt)
    OPPRETT_SERVICEKLAGE,

    // Ved opprettelse av serviceklage
    KLASSIFISER_SERVICEKLAGE,

    // Ved klassifisering av serviceklage
    OPPDATER_SERVICEKLAGE,
}
