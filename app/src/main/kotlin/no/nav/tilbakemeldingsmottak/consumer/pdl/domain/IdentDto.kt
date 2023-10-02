package no.nav.tilbakemeldingsmottak.consumer.pdl.domain

data class IdentDto(
    val ident: String,
    val gruppe: String,
    val historisk: Boolean
)

