package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.OpprettJournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettJournalpostRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpprettServiceklageService {

    @Value("${email_nav_support_address}")
    private String emailToAddress;
    @Value("${email_from_address}")
    private String emailFromAddress;

    private final ServiceklageRepository serviceklageRepository;
    private final OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;
    private final OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper;
    private final OpprettJournalpostConsumer opprettJournalpostConsumer;
    private final OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper;
    private final OppgaveConsumer oppgaveConsumer;
    private final PdfService pdfService;

    public OpprettServiceklageResponse opprettServiceklage(OpprettServiceklageRequest request) throws DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        byte[] fysiskDokument = pdfService.opprettPdf(request);

        if (request.getKlagetyper().contains(Klagetype.LOKALT_NAV_KONTOR) &&
                GjelderSosialhjelpType.JA.equals(request.getGjelderSosialhjelp())) {
            behandleKommunalKlage(fysiskDokument);
            return OpprettServiceklageResponse.builder()
                    .message("Klagen er en kommunal klage, videresendt på mail til " + emailToAddress)
                    .build();
        }

        OpprettJournalpostRequestTo opprettJournalpostRequestTo = opprettJournalpostRequestToMapper.map(request, fysiskDokument);
        OpprettJournalpostResponseTo opprettJournalpostResponseTo = opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo);
        log.info("Journalpost med journalpostId={} opprettet", opprettJournalpostResponseTo.getJournalpostId());

        OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.map(serviceklage.getKlagenGjelderId(), request.getPaaVegneAv(), opprettJournalpostResponseTo);
        OpprettOppgaveResponseTo opprettOppgaveResponseTo = oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo);
        log.info("Oppgave med oppgaveId={} opprettet", opprettOppgaveResponseTo.getId());

        serviceklage.setJournalpostId(opprettJournalpostResponseTo.getJournalpostId());
        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());

        return OpprettServiceklageResponse.builder()
                .message("Serviceklage opprettet")
                .serviceklageId(serviceklage.getServiceklageId().toString())
                .journalpostId(serviceklage.getJournalpostId())
                .oppgaveId(opprettOppgaveResponseTo.getId())
                .build();
    }
}
