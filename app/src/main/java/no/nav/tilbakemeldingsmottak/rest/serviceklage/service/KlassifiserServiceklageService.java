package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.ServiceklageIkkeFunnetException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KlassifiserServiceklageService {

    private final ServiceklageRepository serviceklageRepository;
    private final OppgaveConsumer oppgaveConsumer;
    private final EndreOppgaveRequestToMapper endreOppgaveRequestToMapper;

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, String journalpostId)  {
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

        log.info("Serviceklage med serviceklageId={} er klassifisert", serviceklage.getServiceklageId());

        HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(serviceklage.getOppgaveId());
        EndreOppgaveRequestTo endreOppgaveRequestTo = endreOppgaveRequestToMapper.map(hentOppgaveResponseTo);
        oppgaveConsumer.endreOppgave(endreOppgaveRequestTo);
    }
}
