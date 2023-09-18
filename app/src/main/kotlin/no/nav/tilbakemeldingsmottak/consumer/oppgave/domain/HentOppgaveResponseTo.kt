package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain

data class HentOppgaveResponseTo(
    val id: String,
    val tildeltEnhetsnr: String,
    val tema: String,
    val oppgavetype: String,
    val versjon: String,
    val prioritet: String,
    val status: String,
    val aktivDato: String,

    val aktoerId: String? = null,
    val orgnr: String? = null,
    val journalpostId: String? = null,
)
