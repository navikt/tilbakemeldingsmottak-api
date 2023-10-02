package no.nav.tilbakemeldingsmottak.consumer.joark.domain

// Swagger: https://dokarkiv.dev.intern.nav.no/swagger-ui/index.html#/journalpostapi/opprettJournalpost
// Java: https://github.com/navikt/dokarkiv/blob/master/journalpost/src/main/java/no/nav/dokarkiv/journalpost/v1/api/opprettjournalpost/OpprettJournalpostRequest.java
data class OpprettJournalpostRequestTo(
    val journalpostType: JournalpostType,
    val bruker: Bruker,
    val sak: Sak,
    val tema: String,
    val kanal: String,
    val journalfoerendeEnhet: String,
    val avsenderMottaker: AvsenderMottaker,
    val tittel: String,
    val dokumenter: List<Dokument> = listOf(),

    val behandlingstema: String? = null,
    val eksternReferanseId: String? = null,
    val tilleggsopplysninger: List<Tilleggsopplysning> = listOf(),
)
