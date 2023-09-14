package no.nav.tilbakemeldingsmottak.consumer.joark.domain

data class DokumentVariant(
    val filtype: String,
    val variantformat: String,
    val fysiskDokument: ByteArray
)
