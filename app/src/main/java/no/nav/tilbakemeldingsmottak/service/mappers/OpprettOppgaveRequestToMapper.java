package no.nav.tilbakemeldingsmottak.service.mappers;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
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

    public OpprettOppgaveRequestTo map(OpprettServiceklageRequest request, OpprettJournalpostResponseTo opprettJournalpostResponseTo) {
        return OpprettOppgaveRequestTo.builder()
                .tildeltEnhetsnr(TILDELT_ENHETSNR)
                .prioritet(PRIORITET)
//                .aktoerId(mapAktoerId(request))
                .aktivDato(LocalDate.now().toString())
                .journalpostId(opprettJournalpostResponseTo.getJournalpostId())
                .tema(TEMA)
                .oppgavetype(opprettJournalpostResponseTo.getJournalstatus().equals("ENDELIG") ? OPPGAVETYPE_VUR : OPPGAVETYPE_JFR)
                .build();
    }

    private String mapAktoerId(OpprettServiceklageRequest request) {
        switch (request.getPaaVegneAv()) {
            case PRIVATPERSON:
                return request.getInnmelder().getPersonnummer();
            case ANNEN_PERSON:
                return request.getPaaVegneAvPerson().getPersonnummer();
            case BEDRIFT:
                return request.getPaaVegneAvBedrift().getOrganisasjonsnummer();
            default:
                return null;
        }
    }
}
