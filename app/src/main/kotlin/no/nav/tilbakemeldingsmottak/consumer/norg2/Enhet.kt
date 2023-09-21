package no.nav.tilbakemeldingsmottak.consumer.norg2

// FIXME: Deserialization only works in this order
data class Enhet(
    val navn: String? = null,
    val enhetNr: String? = null,
    val status: String? = null
)
