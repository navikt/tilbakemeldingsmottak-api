package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import org.springframework.stereotype.Component;

@Component
public class EndreOppgaveRequestToMapper {
    private static final String STATUS_FERDIGSTILT = "FERDIGSTILT";

    public EndreOppgaveRequestTo.EndreOppgaveRequestToBuilder createBaseRequest(HentOppgaveResponseTo hentOppgaveResponseTo) {
        return EndreOppgaveRequestTo.builder()
                .aktivDato(hentOppgaveResponseTo.getAktivDato())
                .id(hentOppgaveResponseTo.getId())
                .journalpostId(hentOppgaveResponseTo.getJournalpostId())
                .oppgavetype(hentOppgaveResponseTo.getOppgavetype())
                .prioritet(hentOppgaveResponseTo.getPrioritet())
                .tildeltEnhetsnr(hentOppgaveResponseTo.getTildeltEnhetsnr())
                .versjon(hentOppgaveResponseTo.getVersjon());
    }

    public EndreOppgaveRequestTo mapFerdigstillRequest(HentOppgaveResponseTo hentOppgaveResponseTo) {
        return createBaseRequest(hentOppgaveResponseTo)
                .tema(hentOppgaveResponseTo.getTema())
                .status(STATUS_FERDIGSTILT)
                .build();
    }

    public EndreOppgaveRequestTo mapEndreTemaRequest(HentOppgaveResponseTo hentOppgaveResponseTo, String tema) {
        return createBaseRequest(hentOppgaveResponseTo)
                .tema(tema)
                .build();
    }
}
