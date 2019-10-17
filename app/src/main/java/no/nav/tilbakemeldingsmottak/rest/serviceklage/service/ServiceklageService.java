package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfCreator.opprettPdf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.OpprettJournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettJournalpostRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;
    private OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;
    private OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper;
    private OpprettJournalpostConsumer opprettJournalpostConsumer;
    private OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper;
    private OppgaveConsumer oppgaveConsumer;
    private EndreOppgaveRequestToMapper endreOppgaveRequestToMapper;

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository,
                               OpprettServiceklageRequestMapper opprettServiceklageRequestMapper,
                               OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper,
                               OpprettJournalpostConsumer opprettJournalpostConsumer,
                               OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper,
                               OppgaveConsumer oppgaveConsumer,
                               EndreOppgaveRequestToMapper endreOppgaveRequestToMapper) {
        this.serviceklageRepository = serviceklageRepository;
        this.opprettServiceklageRequestMapper = opprettServiceklageRequestMapper;
        this.opprettJournalpostRequestToMapper = opprettJournalpostRequestToMapper;
        this.opprettJournalpostConsumer = opprettJournalpostConsumer;
        this.opprettOppgaveRequestToMapper = opprettOppgaveRequestToMapper;
        this.oppgaveConsumer = oppgaveConsumer;
        this.endreOppgaveRequestToMapper = endreOppgaveRequestToMapper;
    }

    public Serviceklage opprettServiceklage(OpprettServiceklageRequest request) throws DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        byte[] fysiskDokument = opprettPdf(request);

        OpprettJournalpostRequestTo opprettJournalpostRequestTo = opprettJournalpostRequestToMapper.map(request, fysiskDokument);
        OpprettJournalpostResponseTo opprettJournalpostResponseTo = opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo);

        OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.map(serviceklage.getKlagenGjelderId(), request.getPaaVegneAv(), opprettJournalpostResponseTo);
        OpprettOppgaveResponseTo opprettOppgaveResponseTo = oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo);

        serviceklage.setJournalpostId(opprettJournalpostResponseTo.getJournalpostId());
        serviceklage.setOppgaveId(opprettOppgaveResponseTo.getId());
        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());

        return serviceklage;
    }

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, String journalpostId, String oppgaveId)  {
        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage == null) {
            serviceklage = new Serviceklage();
            serviceklage.setJournalpostId(journalpostId);
            serviceklage.setOppgaveId(oppgaveId);
            serviceklage.setDatoOpprettet(LocalDateTime.now());
            serviceklage.setKlagenGjelderId("01010096460");
        } else {
            if (!serviceklage.getOppgaveId().equals(oppgaveId)) {
                serviceklage.setOppgaveId(oppgaveId);
            }
        }

        serviceklage.setErServiceklage(request.getErServiceklage());
        if (request.getErServiceklage().contains("Ja")) {
            serviceklage.setGjelder(null);
            serviceklage.setKanal(request.getKanal());
            serviceklage.setPaaklagetEnhet(request.getPaaklagetEnhet());
            serviceklage.setBehandlendeEnhet(request.getBehandlendeEnhet());
            serviceklage.setYtelseTjeneste(request.getYtelseTjeneste());
            serviceklage.setTema(request.getTema());
            serviceklage.setUtfall(request.getUtfall());
            serviceklage.setSvarmetode(String.join(",", request.getSvarmetode()));
        } else {
            serviceklage.setGjelder(request.getGjelder());
            serviceklage.setKanal(null);
            serviceklage.setPaaklagetEnhet(null);
            serviceklage.setBehandlendeEnhet(null);
            serviceklage.setYtelseTjeneste(null);
            serviceklage.setTema(null);
            serviceklage.setUtfall(null);
            serviceklage.setSvarmetode(null);
        }

        serviceklageRepository.save(serviceklage);

        log.info("Serviceklage med serviceklageId={} er klassifisert", serviceklage.getServiceklageId());

        if (isNotBlank(oppgaveId)) {
            HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
            EndreOppgaveRequestTo endreOppgaveRequestTo = endreOppgaveRequestToMapper.map(hentOppgaveResponseTo);
            oppgaveConsumer.endreOppgave(endreOppgaveRequestTo);
            log.info("Oppgave med oppgaveId={} er ferdigstilt", oppgaveId);
        }
    }

    public Serviceklage hentServiceklage(String journalpostId) {
        return serviceklageRepository.findByJournalpostId(journalpostId);
    }
}
