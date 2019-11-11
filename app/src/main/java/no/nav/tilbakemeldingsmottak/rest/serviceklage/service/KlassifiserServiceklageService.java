package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class KlassifiserServiceklageService {

    private final ServiceklageRepository serviceklageRepository;
    private final OppgaveConsumer oppgaveConsumer;
    private final EndreOppgaveRequestToMapper endreOppgaveRequestToMapper;

    private static final String FERDIGSTILT = "FERDIGSTILT";
    private static final String JA = "Ja";

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, String journalpostId, String oppgaveId)  {

        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage == null) {
            serviceklage = new Serviceklage();
            serviceklage.setJournalpostId(journalpostId);
            serviceklage.setOpprettetDato(LocalDateTime.now());
        }

        KlassifiserServiceklageRequest.Answers answers = request.getAnswers();

        // TODO: Fiks der flere mulige spørsmål lagres i samme kolonne i DB (XXX_UTDYPNING)

        serviceklage.setBehandlesSomServiceklage(answers.getBehandlesSomServiceklage());
        serviceklage.setFremmetDato(LocalDateTime.parse(answers.getFremmetDato()));
        serviceklage.setInnsender(answers.getInnsender());
        serviceklage.setKanal(answers.getKanal());
        serviceklage.setEnhetsnummerPaaklaget(answers.getEnhetsnummerPaaklaget());
        serviceklage.setEnhetsnummerBehandlende(JA.equals(answers.getPaaklagetEnhetErBehandlende()) ?
                answers.getEnhetsnummerPaaklaget() : answers.getEnhetsnummerBehandlende());
        serviceklage.setGjelder(answers.getGjelder());
        serviceklage.setYtelse(answers.getYtelse());
        serviceklage.setTema(answers.getTema());
        serviceklage.setUtfall(answers.getUtfall());
        serviceklage.setSvarmetode(answers.getSvarmetode());
        serviceklage.setSvarmetodeUtdypning(answers.getSvarIkkeNoedvendig());

        serviceklageRepository.save(serviceklage);

        log.info("Serviceklage med serviceklageId={} er klassifisert", serviceklage.getServiceklageId());

        if (isNotBlank(oppgaveId)) {
            HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
            if (!FERDIGSTILT.equals(hentOppgaveResponseTo.getStatus())) {
                EndreOppgaveRequestTo endreOppgaveRequestTo = endreOppgaveRequestToMapper.map(hentOppgaveResponseTo);
                oppgaveConsumer.endreOppgave(endreOppgaveRequestTo);
                log.info("Ferdigstilt oppgave med oppgaveId={}", oppgaveId);
            } else {
                log.info("Oppgave med oppgaveId={} er allerede ferdigstilt");
            }
        }
    }
}
