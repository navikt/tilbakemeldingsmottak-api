package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support

import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo
import org.springframework.stereotype.Component

@Component
class EndreOppgaveRequestToMapper {
    companion object {
        private const val STATUS_FERDIGSTILT = "FERDIGSTILT"
    }

    fun createBaseRequest(hentOppgaveResponseTo: HentOppgaveResponseTo): EndreOppgaveRequestTo {
        return EndreOppgaveRequestTo(
            aktivDato = hentOppgaveResponseTo.aktivDato,
            id = hentOppgaveResponseTo.id,
            journalpostId = hentOppgaveResponseTo.journalpostId,
            oppgavetype = hentOppgaveResponseTo.oppgavetype,
            prioritet = hentOppgaveResponseTo.prioritet,
            tildeltEnhetsnr = hentOppgaveResponseTo.tildeltEnhetsnr,
            versjon = hentOppgaveResponseTo.versjon,
            tema = hentOppgaveResponseTo.tema,
            status = hentOppgaveResponseTo.status
        )
    }

    fun mapFerdigstillRequest(hentOppgaveResponseTo: HentOppgaveResponseTo): EndreOppgaveRequestTo {
        return createBaseRequest(hentOppgaveResponseTo).copy(status = STATUS_FERDIGSTILT)
    }
    
}
