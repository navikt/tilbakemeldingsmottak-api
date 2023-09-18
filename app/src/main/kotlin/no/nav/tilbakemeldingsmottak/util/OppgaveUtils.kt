package no.nav.tilbakemeldingsmottak.util

import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode
import no.nav.tilbakemeldingsmottak.exceptions.NoContentException

object OppgaveUtils {
    private val FERDIGSTILT = "FERDIGSTILT"

    fun assertIkkeFerdigstilt(hentOppgaveResponseTo: HentOppgaveResponseTo) {
        if (FERDIGSTILT == hentOppgaveResponseTo.status) {
            throw ClientErrorException(
                "Oppgave med oppgaveId=${hentOppgaveResponseTo.id} er allerede ferdigstilt", ErrorCode.OPPGAVE_COMPLETED
            )
        }
    }

    fun assertHarJournalpost(hentOppgaveResponseTo: HentOppgaveResponseTo) {
        if (hentOppgaveResponseTo.journalpostId == null || "" == hentOppgaveResponseTo.journalpostId) {
            throw NoContentException(
                "Oppgave med oppgaveId=${hentOppgaveResponseTo.id} har ikke tilknyttet dokument i arkivet",
                ErrorCode.OPPGAVE_MISSING_JOURNALPOST
            )
        }
    }
}
