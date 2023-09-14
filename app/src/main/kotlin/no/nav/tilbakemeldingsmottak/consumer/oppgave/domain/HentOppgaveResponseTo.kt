package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain

data class HentOppgaveResponseTo(
    val id: String,
    val aktoerId: String,
    val orgnr: String,
    val tildeltEnhetsnr: String,
    val tema: String,
    val versjon: String,
    val aktivDato: String,
    val prioritet: String,
    val oppgavetype: String,
    val journalpostId: String,
    val status: String
)
