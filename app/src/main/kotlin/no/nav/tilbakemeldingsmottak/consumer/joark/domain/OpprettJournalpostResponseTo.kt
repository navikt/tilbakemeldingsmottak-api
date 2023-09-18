package no.nav.tilbakemeldingsmottak.consumer.joark.domain

// Swagger: https://dokarkiv.dev.intern.nav.no/swagger-ui/index.html
// Java: https://github.com/navikt/dokarkiv/blob/master/journalpost/src/main/java/no/nav/dokarkiv/journalpost/v1/api/opprettjournalpost/OpprettJournalpostResponse.java
data class OpprettJournalpostResponseTo(
    val journalpostId: String,
    val journalstatus: String? = null, // FIXME: @Deprecated, ta i bruk journalpostferdigstilt i stedet
    val melding: String? = null // @Hidden i swagger
)
