package no.nav.tilbakemeldingsmottak.consumer.norg2

import com.fasterxml.jackson.annotation.JsonProperty

data class Enhet(
    @JsonProperty("navn") val navn: String? = null,
    @JsonProperty("enhetNr") val enhetNr: String? = null,
    @JsonProperty("status") val status: String? = null
)
