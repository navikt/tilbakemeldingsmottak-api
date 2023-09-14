package no.nav.tilbakemeldingsmottak.consumer.joark.domain

data class OpprettJournalpostRequestTo(
    val journalpostType: JournalpostType,
    val avsenderMottaker: AvsenderMottaker,
    val bruker: Bruker,
    val tema: String,
    val behandlingstema: String? = null,
    val tittel: String,
    val kanal: String,
    val journalfoerendeEnhet: String,
    val eksternReferanseId: String? = null,
    val tilleggsopplysninger: List<Tilleggsopplysning> = listOf(),
    val sak: Sak,
    val dokumenter: List<Dokument> = listOf()
)
