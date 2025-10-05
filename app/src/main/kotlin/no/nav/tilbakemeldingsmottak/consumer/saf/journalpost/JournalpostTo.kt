package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost

import java.time.LocalDateTime

data class JournalpostTo(
    val dokumenter: List<DokumentInfo> = ArrayList(),
    val bruker: Bruker,
    val datoOpprettet: LocalDateTime
) {
    data class DokumentInfo(
        val dokumentInfoId: String,
        val dokumentvarianter: List<Dokumentvariant> = ArrayList()
    )

    data class Dokumentvariant(
        val variantformat: VariantformatTo,
        val saksbehandlerHarTilgang: Boolean
    )

    data class Bruker(
        val id: String
    )
}
