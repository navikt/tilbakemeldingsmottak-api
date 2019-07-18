package no.nav.tilbakemeldingsmottak.service;

import static no.nav.tilbakemeldingsmottak.service.pdf.PdfCreator.opprettPdf;

import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.consumer.joark.OpprettJournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.api.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OpprettOppgaveConsumer;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageIkkeFunnetException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettJournalpostRequestToMapper;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileNotFoundException;

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

    public long opprettServiceklage(OpprettServiceklageRequest request, String authorizationHeader) throws FileNotFoundException , DocumentException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());
        byte[] fysiskDokument = opprettPdf(request);

        OpprettJournalpostRequestTo opprettJournalpostRequestTo = opprettJournalpostRequestToMapper.map(request, fysiskDokument);
        OpprettJournalpostResponseTo opprettJournalpostResponseTo = opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestTo, authorizationHeader);

//        OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.map(request, opprettJournalpostResponseTo);
//        opprettOppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo, authorizationHeader);

        return serviceklage.getServiceklageId();
    }

    public long registrerTilbakemelding(RegistrerTilbakemeldingRequest request, String serviceklageId)  {
        Serviceklage serviceklage = serviceklageRepository.findById(Long.parseLong(serviceklageId))
                .orElseThrow(() -> new ServiceklageIkkeFunnetException(String.format("Kunne ikke finne serviceklage med serviceklageId=%s", serviceklageId)));

        serviceklage.setErServiceklage(request.getErServiceklage());
        if (request.getErServiceklage().equalsIgnoreCase("ja")) {
            serviceklage.setGjelder(null);
            serviceklage.setPaaklagetEnhet(request.getPaaklagetEnhet());
            serviceklage.setBehandlendeEnhet(request.getBehandlendeEnhet());
            serviceklage.setYtelseTjeneste(request.getYtelseTjeneste());
            serviceklage.setTema(request.getTema());
            serviceklage.setUtfall(request.getUtfall());
            serviceklage.setSvarmetode(String.join(",", request.getSvarmetode()));
        } else {
            serviceklage.setGjelder(request.getGjelder());
            serviceklage.setPaaklagetEnhet(null);
            serviceklage.setBehandlendeEnhet(null);
            serviceklage.setYtelseTjeneste(null);
            serviceklage.setTema(null);
            serviceklage.setUtfall(null);
            serviceklage.setSvarmetode(null);
        }

        serviceklageRepository.save(serviceklage);
        log.info("Tilbakemelding registrert for serviceklage med serviceklageId={}", serviceklage.getServiceklageId());

        return serviceklage.getServiceklageId();
    }
}
