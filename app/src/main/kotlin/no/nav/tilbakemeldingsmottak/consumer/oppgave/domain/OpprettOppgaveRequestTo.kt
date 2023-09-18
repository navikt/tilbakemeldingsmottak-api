package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain

data class OpprettOppgaveRequestTo(
    val tema: String,
    val oppgavetype: String,
    val prioritet: String,
    val aktivDato: String,

    val tildeltEnhetsnr: String? = null,
    val opprettetAvEnhetsnr: String? = null,
    val aktoerId: String? = null,
    val journalpostId: String? = null,
    val journalpostkilde: String? = null,
    val behandlesAvApplikasjon: String? = null,
    val saksreferanse: String? = null,
    val orgnr: String? = null,
    val bnr: String? = null,
    val samhandlernr: String? = null,
    val tilordnetRessurs: String? = null,
    val beskrivelse: String? = null,
    val temagruppe: String? = null,
    val behandlingstema: String? = null,
    val behandlingstype: String? = null,
    val mappeId: String? = null,
    val fristFerdigstillelse: String? = null,
)

