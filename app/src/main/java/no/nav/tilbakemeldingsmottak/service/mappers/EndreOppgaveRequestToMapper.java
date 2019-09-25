package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import org.springframework.stereotype.Component;

@Component
public class EndreOppgaveRequestToMapper {
    private static final String STATUS_FERDIGSTILT = "FERDIGSTILT";

    public EndreOppgaveRequestTo map(HentOppgaveResponseTo hentOppgaveResponseTo) {
        return EndreOppgaveRequestTo.builder()
                .aktivDato(hentOppgaveResponseTo.getAktivDato())
                .id(hentOppgaveResponseTo.getId())
                .journalpostId(hentOppgaveResponseTo.getJournalpostId())
                .oppgavetype(hentOppgaveResponseTo.getOppgavetype())
                .prioritet(hentOppgaveResponseTo.getPrioritet())
                .tema(hentOppgaveResponseTo.getTema())
                .tildeltEnhetsnr(hentOppgaveResponseTo.getTildeltEnhetsnr())
                .versjon(hentOppgaveResponseTo.getVersjon())
                .status(STATUS_FERDIGSTILT)
                .build();
    }
}
