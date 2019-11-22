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
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.GjelderSosialhjelpType;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettJournalpostRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettServiceklageRequestMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.ServiceklageMailHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpprettServiceklageService {

    private final ServiceklageRepository serviceklageRepository;
    private final OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;
    private final OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper;
    private final OpprettJournalpostConsumer opprettJournalpostConsumer;
    private final OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper;
    private final OppgaveConsumer oppgaveConsumer;
    private final PdfService pdfService;
    private final ServiceklageMailHelper mailHelper;

    public static final String SUBJECT_KOMMUNAL_KLAGE = "Kommunal klage mottatt via serviceklageskjema på nav.no";
    public static final String TEXT_KOMMUNAL_KLAGE = "En kommunal klage har blitt sendt inn via serviceklageskjema på nav.no. Denne har ikke blitt journalført eller lagret i serviceklagedatabasen. Feilsendt klage ligger vedlagt.";
    public static final String SUBJECT_JOURNALPOST_FEILET = "Automatisk journalføring av serviceklage feilet";
    public static final String TEXT_JOURNALPOST_FEILET= "Manuell journalføring og opprettelse av oppgave kreves. Klagen ligger vedlagt.";
    public static final String SUBJECT_OPPGAVE_FEILET = "Automatisk opprettelse av oppgave feilet";
    public static final String TEXT_OPPGAVE_FEILET= "Manuell opprettelse av oppgave kreves for serviceklage med journalpostId=";

    @Value("${email_serviceklage_address}")
    private String toAddress;
    @Value("${email_from_address}")
    private String fromAddress;

    public OpprettServiceklageResponse opprettServiceklage(OpprettServiceklageRequest request) throws DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        byte[] fysiskDokument = pdfService.opprettPdf(request);

        if (isKommunalKlage(request)) {
            mailHelper.sendEmail(fromAddress, toAddress, SUBJECT_KOMMUNAL_KLAGE, TEXT_KOMMUNAL_KLAGE, fysiskDokument);
            log.info("Klagen er en kommunal klage, videresendt på mail til " + toAddress);
            return OpprettServiceklageResponse.builder()
                    .message("Klagen er en kommunal klage, videresendt på mail til " + toAddress)
                    .build();
        }

        OpprettJournalpostResponseTo opprettJournalpostResponseTo = forsoekOpprettJournalpost(request, fysiskDokument);
        log.info("Journalpost med journalpostId={} opprettet", opprettJournalpostResponseTo.getJournalpostId());

        serviceklage.setJournalpostId(opprettJournalpostResponseTo.getJournalpostId());
        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());

        OpprettOppgaveResponseTo opprettOppgaveResponseTo = forsoekOpprettOppgave(serviceklage.getKlagenGjelderId(), request.getPaaVegneAv(), opprettJournalpostResponseTo);
        log.info("Oppgave med oppgaveId={} opprettet", opprettOppgaveResponseTo.getId());

        return OpprettServiceklageResponse.builder()
                .message("Serviceklage opprettet")
                .serviceklageId(serviceklage.getServiceklageId().toString())
                .journalpostId(serviceklage.getJournalpostId())
                .oppgaveId(opprettOppgaveResponseTo.getId())
                .build();
    }

    private boolean isKommunalKlage(OpprettServiceklageRequest request) {
        return request.getKlagetyper().contains(Klagetype.LOKALT_NAV_KONTOR) &&
                GjelderSosialhjelpType.JA.equals(request.getGjelderSosialhjelp());
    }

    private OpprettJournalpostResponseTo forsoekOpprettJournalpost(OpprettServiceklageRequest request, byte[] fysiskDokument) {
        try {
            OpprettJournalpostRequestTo opprettJournalpostRequestTo = opprettJournalpostRequestToMapper.map(request, fysiskDokument);
            return opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo);
        } catch (OpprettJournalpostFunctionalException | OpprettJournalpostTechnicalException e) {
            mailHelper.sendEmail(fromAddress, toAddress, SUBJECT_JOURNALPOST_FEILET, TEXT_JOURNALPOST_FEILET, fysiskDokument);
            throw new EksterntKallException("Feil ved opprettelse av journalpost, klage videresendt til " + toAddress);
        }
    }

    private OpprettOppgaveResponseTo forsoekOpprettOppgave(String id, PaaVegneAvType paaVegneAvType, OpprettJournalpostResponseTo opprettJournalpostResponseTo) {
        try {
            OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.map(id, paaVegneAvType, opprettJournalpostResponseTo);
            return oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo);
        } catch (OpprettOppgaveFunctionalException | OpprettOppgaveTechnicalException e) {
            mailHelper.sendEmail(fromAddress, toAddress, SUBJECT_OPPGAVE_FEILET, TEXT_OPPGAVE_FEILET + opprettJournalpostResponseTo.getJournalpostId());
            throw new EksterntKallException("Feil ved opprettelse av oppgave, journalpostId videresendt til " + toAddress);
        }
    }
}