package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import no.nav.tilbakemeldingsmottak.consumer.aktoer.AktoerConsumer;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;

@Component
public class OpprettOppgaveRequestToMapper {

    private static final String TILDELT_ENHETSNR = "4200";
    private static final String PRIORITET = "NORM";
    private static final String TEMA = "SER";
    private static final String OPPGAVETYPE_VUR = "VUR";
    private static final String OPPGAVETYPE_JFR = "JFR";
    private static final String JOURNALSTATUS_ENDELIG = "ENDELIG";
    private static final Long DAGER_FRIST = 18L;

    private AktoerConsumer aktoerConsumer;

    @Inject
    public OpprettOppgaveRequestToMapper(AktoerConsumer aktoerConsumerService) {
        this.aktoerConsumer = aktoerConsumerService;
    }

    public OpprettOppgaveRequestTo map(String klagenGjelderId, PaaVegneAvType paaVegneAvType, OpprettJournalpostResponseTo opprettJournalpostResponseTo) {
        return OpprettOppgaveRequestTo.builder()
                .tildeltEnhetsnr(TILDELT_ENHETSNR)
                .prioritet(PRIORITET)
                .aktoerId(paaVegneAvType.equals(PaaVegneAvType.BEDRIFT) ? null : aktoerConsumer.hentAktoerIdForIdent(klagenGjelderId).get(klagenGjelderId).getFirstIdent())
                .orgnr(paaVegneAvType.equals(PaaVegneAvType.BEDRIFT) ? klagenGjelderId : null)
                .aktivDato(LocalDate.now().toString())
//                .journalpostId(opprettJournalpostResponseTo.getJournalpostId())
                .tema(TEMA)
                .oppgavetype(JOURNALSTATUS_ENDELIG.equals(opprettJournalpostResponseTo.getJournalstatus()) ? OPPGAVETYPE_VUR : OPPGAVETYPE_JFR)
                .fristFerdigstillelse(LocalDate.now().plusDays(DAGER_FRIST).toString())
                .build();
    }
}
