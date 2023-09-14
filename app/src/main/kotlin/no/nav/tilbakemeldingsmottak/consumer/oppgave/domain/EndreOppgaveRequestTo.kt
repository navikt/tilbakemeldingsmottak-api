package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain

data class EndreOppgaveRequestTo(
    val id: String,
    val tildeltEnhetsnr: String,
    val tema: String,
    val versjon: String,
    val aktivDato: String,
    val prioritet: String,
    val oppgavetype: String,
    val journalpostId: String,
    val status: String
)
