package no.nav.tilbakemeldingsmottak.consumer.joark.domain

// Swagger: https://dokarkiv.dev.intern.nav.no/swagger-ui/index.html
data class DokumentVariant(
    val filtype: String,
    val variantformat: String,
    val fysiskDokument: ByteArray
)
