package no.nav.tilbakemeldingsmottak.service;

import static no.nav.tilbakemeldingsmottak.service.pdf.PdfCreator.opprettPdf;

import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.consumer.joark.OpprettJournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OpprettOppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageIkkeFunnetException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettJournalpostRequestToMapper;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;
    private OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;
    private OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper;
    private OpprettJournalpostConsumer opprettJournalpostConsumer;
    private OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper;
    private OpprettOppgaveConsumer opprettOppgaveConsumer;

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository,
                               OpprettServiceklageRequestMapper opprettServiceklageRequestMapper,
                               OpprettJournalpostRequestToMapper opprettJournalpostRequestToMapper,
                               OpprettJournalpostConsumer opprettJournalpostConsumer,
                               OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper,
                               OpprettOppgaveConsumer opprettOppgaveConsumer) {
        this.serviceklageRepository = serviceklageRepository;
        this.opprettServiceklageRequestMapper = opprettServiceklageRequestMapper;
        this.opprettJournalpostRequestToMapper = opprettJournalpostRequestToMapper;
        this.opprettJournalpostConsumer = opprettJournalpostConsumer;
        this.opprettOppgaveRequestToMapper = opprettOppgaveRequestToMapper;
        this.opprettOppgaveConsumer = opprettOppgaveConsumer;
    }

    public long opprettServiceklage(OpprettServiceklageRequest request) throws DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        byte[] fysiskDokument = opprettPdf(request);

        OpprettJournalpostRequestTo opprettJournalpostRequestTo = opprettJournalpostRequestToMapper.map(request, fysiskDokument);
        OpprettJournalpostResponseTo opprettJournalpostResponseTo = opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo);

        OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.map(serviceklage.getKlagenGjelderId(), request.getPaaVegneAv(), opprettJournalpostResponseTo);
        opprettOppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo);

        serviceklage.setJournalpostId(opprettJournalpostResponseTo.getJournalpostId());
        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());

        return serviceklage.getServiceklageId();
    }

    public void registrerTilbakemelding(RegistrerTilbakemeldingRequest request, String journalpostId)  {
        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage == null) {
            throw new ServiceklageIkkeFunnetException(String.format("Kunne ikke finne serviceklage med journalpostId=%s", journalpostId));
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
        log.info("Tilbakemelding registrert for serviceklage med serviceklageId={}", serviceklage.getServiceklageId());
    }

    public Serviceklage hentServiceklage(String journalpostId) {
        return serviceklageRepository.findByJournalpostId(journalpostId);
    }
}
