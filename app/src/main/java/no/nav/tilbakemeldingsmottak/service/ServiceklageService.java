package no.nav.tilbakemeldingsmottak.service;

import static no.nav.tilbakemeldingsmottak.service.pdf.PdfCreator.opprettPdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.consumer.joark.OpprettJournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OpprettOppgaveConsumer;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
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
        Document pdf = opprettPdf(request);

//        opprettJournalpostConsumer.opprettJournalpost(opprettJournalpostRequestToMapper.map(request), authorizationHeader);
        opprettOppgaveConsumer.opprettOppgave(opprettOppgaveRequestToMapper.map(request), authorizationHeader);

        return serviceklage.getServiceklageId();
    }
}
