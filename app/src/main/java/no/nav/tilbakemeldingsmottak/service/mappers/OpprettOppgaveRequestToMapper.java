package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.consumer.joark.api.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.api.OpprettOppgaveRequestTo;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OpprettOppgaveRequestToMapper {

    private static final String TILDELT_ENHETSNR = "4200";
    private static final String PRIORITET = "NORM";
    private static final String TEMA= "SER";
    private static final String OPPGAVETYPE_VUR = "VUR";
    private static final String OPPGAVETYPE_JFR = "JFR";
    private static final String JOURNALSTATUS_ENDELIG= "ENDELIG";

    public OpprettOppgaveRequestTo map(String klagenGjelderId, OpprettJournalpostResponseTo opprettJournalpostResponseTo) {
        return OpprettOppgaveRequestTo.builder()
                .tildeltEnhetsnr(TILDELT_ENHETSNR)
                .prioritet(PRIORITET)
                .aktoerId(klagenGjelderId) // må mappes
                .aktivDato(LocalDate.now().toString())
                .journalpostId(opprettJournalpostResponseTo.getJournalpostId())
                .tema(TEMA)
                .oppgavetype(JOURNALSTATUS_ENDELIG.equals(opprettJournalpostResponseTo.getJournalstatus()) ? OPPGAVETYPE_VUR : OPPGAVETYPE_JFR)
                .build();
    }
}
