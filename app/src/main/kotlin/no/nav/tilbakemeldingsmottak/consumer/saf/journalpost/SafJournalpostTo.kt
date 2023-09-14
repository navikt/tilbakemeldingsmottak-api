package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost

data class SafJournalpostTo(
    val dokumenter: List<DokumentInfo> = ArrayList(),
    val bruker: Bruker,
    val datoOpprettet: String
) {
    data class DokumentInfo(
        val dokumentInfoId: String,
        val dokumentvarianter: List<Dokumentvariant> = ArrayList()
    )

    data class Dokumentvariant(
        val variantformat: String,
        val saksbehandlerHarTilgang: Boolean
    )

    data class Bruker(
        val id: String
    )
}
