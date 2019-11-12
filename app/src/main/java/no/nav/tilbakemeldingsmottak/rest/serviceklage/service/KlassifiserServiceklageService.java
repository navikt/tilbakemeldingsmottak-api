package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static org.apache.commons.lang3.StringUtils.isBlank;
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
    private static final String ANNET= "Annet";

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, String journalpostId, String oppgaveId)  {

        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage == null) {
            serviceklage = createNewServiceklage(journalpostId);
        }

        KlassifiserServiceklageRequest.Answers answers = request.getAnswers();

        serviceklage.setBehandlesSomServiceklage(answers.getBehandlesSomServiceklage());
        serviceklage.setBehandlesSomServiceklageUtdypning(answers.getBehandlesSomServiceklageUtdypning());
        serviceklage.setFremmetDato(LocalDateTime.parse(answers.getFremmetDato()));
        serviceklage.setInnsender(answers.getInnsender());
        serviceklage.setKanal(answers.getKanal());
        serviceklage.setKanal(answers.getKanalUtdypning());
        serviceklage.setEnhetsnummerPaaklaget(answers.getEnhetsnummerPaaklaget());
        serviceklage.setEnhetsnummerBehandlende(JA.equals(answers.getPaaklagetEnhetErBehandlende()) ?
                answers.getEnhetsnummerPaaklaget() : answers.getEnhetsnummerBehandlende());
        serviceklage.setGjelder(answers.getGjelder());
        serviceklage.setBeskrivelse(answers.getBeskrivelse());
        serviceklage.setYtelse(answers.getYtelse());
        serviceklage.setTema(answers.getTema());
        serviceklage.setTemaUtdypning(mapTemaUtdypning(answers));
        serviceklage.setUtfall(answers.getUtfall());
        serviceklage.setAarsak(answers.getAarsak());
        serviceklage.setTiltak(answers.getTiltak());
        serviceklage.setSvarmetode(answers.getSvarmetode());
        serviceklage.setSvarmetodeUtdypning(mapSvarmetodeUtdypning(answers));

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

    private Serviceklage createNewServiceklage(String journalpostId) {
        Serviceklage serviceklage = new Serviceklage();
        serviceklage.setJournalpostId(journalpostId);
        serviceklage.setOpprettetDato(LocalDateTime.now());
        return serviceklage;
    }

    private String mapTemaUtdypning(KlassifiserServiceklageRequest.Answers answers) {
        if (!isBlank(answers.getVente())) {
            return answers.getVente();
        } else if (!isBlank(answers.getTilgjengelighet())) {
            return answers.getTilgjengelighet();
        } else if (!isBlank(answers.getInformasjon())) {
            return answers.getInformasjon();
        } else if (!isBlank(answers.getVeiledning())) {
            return answers.getVeiledning();
        } else if (!isBlank(answers.getTemaUtdypning())) {
            return answers.getTemaUtdypning();
        } else {
            return null;
        }
    }

    private String mapSvarmetodeUtdypning(KlassifiserServiceklageRequest.Answers answers) {
        if (!isBlank(answers.getSvarIkkeNoedvendig())) {
            if (answers.getSvarIkkeNoedvendig().equals(ANNET)) {
                return answers.getSvarmetodeUtdypning();
            } else {
                return answers.getSvarIkkeNoedvendig();
            }
        } else{
            return null;
        }
    }
}
