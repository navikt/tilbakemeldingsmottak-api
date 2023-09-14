package no.nav.tilbakemeldingsmottak.consumer.joark.domain

data class OpprettJournalpostResponseTo(
    val journalpostId: String,
    val journalstatus: String,
    val melding: String
)
