package no.nav.tilbakemeldingsmottak.consumer.oppgave.domain

data class OpprettOppgaveRequestTo(
    val tildeltEnhetsnr: String,
    val opprettetAvEnhetsnr: String? = null,
    val aktoerId: String,
    val journalpostId: String,
    val journalpostkilde: String? = null,
    val behandlesAvApplikasjon: String? = null,
    val saksreferanse: String? = null,
    val orgnr: String,
    val bnr: String? = null,
    val samhandlernr: String? = null,
    val tilordnetRessurs: String? = null,
    val beskrivelse: String? = null,
    val temagruppe: String? = null,
    val tema: String,
    val behandlingstema: String? = null,
    val oppgavetype: String,
    val behandlingstype: String? = null,
    val mappeId: String? = null,
    val aktivDato: String,
    val fristFerdigstillelse: String,
    val prioritet: String
)

