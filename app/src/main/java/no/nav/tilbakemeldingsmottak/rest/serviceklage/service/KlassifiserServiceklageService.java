package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.exceptions.OppgaveAlleredeFerdigstiltException;
import no.nav.tilbakemeldingsmottak.exceptions.RequestParsingException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private static final String ANNET = "Annet";

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, HentOppgaveResponseTo hentOppgaveResponseTo)  {

        if (FERDIGSTILT.equals(hentOppgaveResponseTo.getStatus())) {
            throw new OppgaveAlleredeFerdigstiltException(String.format("Oppgave med oppgaveId=%s er allerede ferdigstilt",
                    hentOppgaveResponseTo.getId()));
        }

        String journalpostId = hentOppgaveResponseTo.getJournalpostId();
        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage == null) {
            serviceklage = createNewServiceklage(journalpostId);
        }

        updateServiceklage(serviceklage, request);

        serviceklageRepository.save(serviceklage);

        log.info("Serviceklage med serviceklageId={} er klassifisert", serviceklage.getServiceklageId());

        EndreOppgaveRequestTo endreOppgaveRequestTo = endreOppgaveRequestToMapper.map(hentOppgaveResponseTo);
        oppgaveConsumer.endreOppgave(endreOppgaveRequestTo);
        log.info("Ferdigstilt oppgave med oppgaveId={}", hentOppgaveResponseTo.getId());
    }

    private Serviceklage createNewServiceklage(String journalpostId) {
        Serviceklage serviceklage = new Serviceklage();
        serviceklage.setJournalpostId(journalpostId);
        serviceklage.setOpprettetDato(LocalDateTime.now());
        return serviceklage;
    }

    private void updateServiceklage(Serviceklage serviceklage, KlassifiserServiceklageRequest request) {
        serviceklage.setBehandlesSomServiceklage(request.getBehandlesSomServiceklage());
        serviceklage.setBehandlesSomServiceklageUtdypning(request.getBehandlesSomServiceklageUtdypning());
        serviceklage.setFremmetDato(LocalDate.parse(request.getFremmetDato()));
        serviceklage.setInnsender(request.getInnsender());
        serviceklage.setKanal(request.getKanal());
        serviceklage.setKanalUtdypning(request.getKanalUtdypning());
        serviceklage.setEnhetsnummerPaaklaget(extractEnhetsnummer(request.getEnhetsnummerPaaklaget()));
        serviceklage.setEnhetsnummerBehandlende(JA.equals(request.getPaaklagetEnhetErBehandlende()) ?
                extractEnhetsnummer(request.getEnhetsnummerPaaklaget()) :
                extractEnhetsnummer(request.getEnhetsnummerBehandlende()));
        serviceklage.setGjelder(request.getGjelder());
        serviceklage.setBeskrivelse(request.getBeskrivelse());
        serviceklage.setYtelse(request.getYtelse());
        serviceklage.setTema(request.getTema());
        serviceklage.setTemaUtdypning(mapTemaUtdypning(request));
        serviceklage.setUtfall(request.getUtfall());
        serviceklage.setAarsak(request.getAarsak());
        serviceklage.setTiltak(request.getTiltak());
        serviceklage.setSvarmetode(request.getSvarmetode());
        serviceklage.setSvarmetodeUtdypning(mapSvarmetodeUtdypning(request));
        serviceklage.setAvsluttetDato(LocalDateTime.now());
        try {
            serviceklage.setKlassifiseringJson(new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RequestParsingException("Kan ikke konvertere klassifiseringsrequest til JSON-string");
        }
    }

    private String mapTemaUtdypning(KlassifiserServiceklageRequest request) {
        if (!isBlank(request.getVente())) {
            return request.getVente();
        } else if (!isBlank(request.getTilgjengelighet())) {
            return request.getTilgjengelighet();
        } else if (!isBlank(request.getInformasjon())) {
            return request.getInformasjon();
        } else if (!isBlank(request.getVeiledning())) {
            return request.getVeiledning();
        } else if (!isBlank(request.getTemaUtdypning())) {
            return request.getTemaUtdypning();
        } else {
            return null;
        }
    }

    private String mapSvarmetodeUtdypning(KlassifiserServiceklageRequest request) {
        if (!isBlank(request.getSvarIkkeNoedvendig())) {
            if (request.getSvarIkkeNoedvendig().equals(ANNET)) {
                return request.getSvarmetodeUtdypning();
            } else {
                return request.getSvarIkkeNoedvendig();
            }
        } else{
            return null;
        }
    }

    private String extractEnhetsnummer(String enhet) {
        if (isBlank(enhet)) {
            return null;
        }
        int l = enhet.trim().length();
        if (l >= 4) {
            String enhetsnummer = enhet.trim().substring(l-4);
            if (isNumeric(enhetsnummer)) {
                return enhetsnummer;
            }
        }
        throw new InvalidRequestException("Klarer ikke å hente ut enhetsnummer for enhet=" + enhet);
    }
}
