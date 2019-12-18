package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.JournalpostConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.InvalidRequestException;
import no.nav.tilbakemeldingsmottak.exceptions.RequestParsingException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.FeilregistrerSakstilknytningFunctionalException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class KlassifiserServiceklageService {

    private final ServiceklageRepository serviceklageRepository;
    private final OppgaveConsumer oppgaveConsumer;
    private final JournalpostConsumer journalpostConsumer;
    private final EndreOppgaveRequestToMapper endreOppgaveRequestToMapper;

    @Value("${email_serviceklage_address}")
    private String toAddress;
    @Value("${email_from_address}")
    private String fromAddress;

    private static final String KOMMUNAL_KLAGE = "Nei, serviceklagen gjelder kommunale tjenester eller ytelser";
    private static final String FORVALTNINGSKLAGE = "Nei - en forvaltningsklage";

    private static final String JA = "Ja";
    private static final String ANNET = "Annet";

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, HentOppgaveResponseTo hentOppgaveResponseTo)  {
        if (KOMMUNAL_KLAGE.equals(request.getBehandlesSomServiceklage())) {
            log.info("Klagen har blitt markert som en kommunal klage. Journalposten feilregistreres.");
            handterKommunalKlage(hentOppgaveResponseTo);
        } else if (FORVALTNINGSKLAGE.equals(request.getBehandlesSomServiceklage())) {
            log.info("Klagen har blitt markert som en forvaltningsklage.");
        }

        Serviceklage serviceklage = getOrCreateServiceklage(hentOppgaveResponseTo.getJournalpostId());
        updateServiceklage(serviceklage, request);
        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} er klassifisert", serviceklage.getServiceklageId());

        if (!FORVALTNINGSKLAGE.equals(request.getBehandlesSomServiceklage())) {
            ferdigstillOppgave(hentOppgaveResponseTo);
            log.info("Ferdigstilt oppgave med oppgaveId={}", hentOppgaveResponseTo.getId());
        }
    }


    private void handterKommunalKlage(HentOppgaveResponseTo hentOppgaveResponseTo) {
        String journalpostId = hentOppgaveResponseTo.getJournalpostId();
        try {
            journalpostConsumer.feilregistrerSakstilknytning(journalpostId);
        } catch (FeilregistrerSakstilknytningFunctionalException e) {
            log.info("Forsøkte å feilregistrere sakstilknytning for journalpost med id={}, men sakstilknytningen er allerede feilregistrert.", journalpostId);
        }
    }

    private Serviceklage getOrCreateServiceklage(String journalpostId) {
        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage == null) {
            serviceklage = new Serviceklage();
            serviceklage.setJournalpostId(journalpostId);
            serviceklage.setOpprettetDato(LocalDateTime.now());
        }
        return serviceklage;
    }

    private void updateServiceklage(Serviceklage serviceklage, KlassifiserServiceklageRequest request) {
        serviceklage.setBehandlesSomServiceklage(request.getBehandlesSomServiceklage());
        serviceklage.setBehandlesSomServiceklageUtdypning(request.getBehandlesSomServiceklageUtdypning());
        serviceklage.setFremmetDato(request.getFremmetDato() == null ? null : LocalDate.parse(request.getFremmetDato()));
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

    private void ferdigstillOppgave(HentOppgaveResponseTo hentOppgaveResponseTo) {
        EndreOppgaveRequestTo endreOppgaveRequestTo = endreOppgaveRequestToMapper.mapFerdigstillRequest(hentOppgaveResponseTo);
        oppgaveConsumer.endreOppgave(endreOppgaveRequestTo);
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
