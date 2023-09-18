package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain

data class EndreOppgaveRequestTo(
    val versjon: String,

    val id: String? = null,
    val tildeltEnhetsnr: String? = null,
    val tema: String? = null,
    val aktivDato: String? = null,
    val prioritet: String? = null,
    val oppgavetype: String? = null,
    val journalpostId: String? = null,
    val status: String? = null
)
