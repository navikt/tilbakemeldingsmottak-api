package no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support;

import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.pdl.PdlService;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAvEnum;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;

@Component
public class OpprettOppgaveRequestToMapper {

    private static final String KLAGEINSTANS_ENHETSNR = "4200"; //enhet som får alle nye serviceklager
    private static final String FAGPOST_ENHETSNR = "2950"; //enhet som mottar alle sletteoppgavene
    private static final String PRIORITET = "NORM";
    private static final String SERVICEKLAGE_TEMA = "SER";
    private static final String RETTING_TEMA = "RPO";
    private static final String OPPGAVETYPE_VUR = "VUR";
    private static final String OPPGAVETYPE_JFR = "JFR";
    private static final String JOURNALSTATUS_ENDELIG = "ENDELIG";
    private static final String BESKRIVELSE_SLETTING = "Skal slettes da det ikke er en serviceklage";
    private static final Long DAGER_FRIST = 18L;

    private PdlService pdlService;


    @Inject
    public OpprettOppgaveRequestToMapper(PdlService pdlService){
        this.pdlService = pdlService;
    }

    public OpprettOppgaveRequestTo mapServiceklageOppgave(String klagenGjelderId, PaaVegneAvEnum paaVegneAvEnum, OpprettJournalpostResponseTo opprettJournalpostResponseTo) {
        return OpprettOppgaveRequestTo.builder()
                .tildeltEnhetsnr(KLAGEINSTANS_ENHETSNR)
                .prioritet(PRIORITET)
                .aktoerId(paaVegneAvEnum.equals(PaaVegneAvEnum.BEDRIFT) ? null :  pdlService.hentAktorIdForIdent(klagenGjelderId))
                .orgnr(paaVegneAvEnum.equals(PaaVegneAvEnum.BEDRIFT) ? klagenGjelderId : null)
                .aktivDato(LocalDate.now().toString())
                .journalpostId(opprettJournalpostResponseTo.getJournalpostId())
                .tema(SERVICEKLAGE_TEMA)
                .oppgavetype(JOURNALSTATUS_ENDELIG.equals(opprettJournalpostResponseTo.getJournalstatus()) ? OPPGAVETYPE_VUR : OPPGAVETYPE_JFR)
                .fristFerdigstillelse(LocalDate.now().plusDays(DAGER_FRIST).toString())
                .build();
    }

    public OpprettOppgaveRequestTo mapSlettingOppgave(HentOppgaveResponseTo hentOppgaveResponseTo) {
        return OpprettOppgaveRequestTo.builder()
                .tildeltEnhetsnr(FAGPOST_ENHETSNR)
                .prioritet(PRIORITET)
                .aktoerId(hentOppgaveResponseTo.getAktoerId())
                .orgnr(hentOppgaveResponseTo.getOrgnr())
                .beskrivelse(BESKRIVELSE_SLETTING)
                .aktivDato(LocalDate.now().toString())
                .journalpostId(hentOppgaveResponseTo.getJournalpostId())
                .tema(RETTING_TEMA)
                .oppgavetype(OPPGAVETYPE_VUR)
                .fristFerdigstillelse(LocalDate.now().plusDays(DAGER_FRIST).toString())
                .build();
    }
}
