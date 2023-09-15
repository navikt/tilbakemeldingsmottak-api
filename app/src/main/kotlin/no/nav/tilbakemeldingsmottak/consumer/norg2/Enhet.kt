package no.nav.tilbakemeldingsmottak.consumer.norg2

// FIXME: Deserialization only works in this order
data class Enhet(
    val enhetNr: String? = null,
    val navn: String? = null,
    val status: String? = null
)
