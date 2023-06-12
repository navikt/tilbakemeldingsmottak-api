package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklageEventTypeEnum;
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorNotFoundException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.model.Answer;
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.Question;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.common.pdf.PdfService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.EndreOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.OpprettOppgaveRequestToMapper;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.support.ServiceklageMailHelper;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.NONE;
import static no.nav.tilbakemeldingsmottak.util.SkjemaUtils.getQuestionById;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Service
@Slf4j
@RequiredArgsConstructor
public class KlassifiserServiceklageService {

    private static final String KOMMUNAL_KLAGE = "Nei, serviceklagen gjelder kommunale tjenester eller ytelser";
    private static final String FORVALTNINGSKLAGE = "Nei, en forvaltningsklage";
    private static final String SKRIV_TIL_OSS = "Nei, Skriv til oss";
    private static final String JA = "Ja";
    private static final String ANNET = "Annet";
    private final ServiceklageRepository serviceklageRepository;
    private final OppgaveConsumer oppgaveConsumer;
    private final EndreOppgaveRequestToMapper endreOppgaveRequestToMapper;
    private final HentSkjemaService hentSkjemaService;
    private final PdfService pdfService;
    private final ServiceklageMailHelper mailHelper;
    private final OidcUtils oicdUtils;
    private final OpprettOppgaveRequestToMapper opprettOppgaveRequestToMapper;
    private final ServiceklagerBigQuery serviceklagerBigQuery;
    @Value("${email_serviceklage_address}")
    private String toAddress;
    @Value("${email_from_address}")
    private String fromAddress;

    public void klassifiserServiceklage(KlassifiserServiceklageRequest request, HentOppgaveResponseTo hentOppgaveResponseTo) {
        if (KOMMUNAL_KLAGE.equals(request.getBEHANDLESSOMSERVICEKLAGE())) {
            log.info("Klagen har blitt markert som en kommunal klage. Oppretter oppgave om sletting av dokument.");
            opprettSlettingOppgave(hentOppgaveResponseTo);
        }

        Serviceklage serviceklage = getOrCreateServiceklage(hentOppgaveResponseTo.getJournalpostId());
        updateServiceklage(serviceklage, request);
        serviceklageRepository.save(serviceklage);
        serviceklagerBigQuery.insertServiceklage(serviceklage, ServiceklageEventTypeEnum.KLASSIFISER_SERVICEKLAGE);

        log.info("Serviceklage med serviceklageId={} er klassifisert som {}", serviceklage.getServiceklageId(), serviceklage.getTema());

        log.info("Ferdigstille oppgave med oppgaveId={} og versjonsnummer={}", hentOppgaveResponseTo.getId(), hentOppgaveResponseTo.getVersjon());
        ferdigstillOppgave(hentOppgaveResponseTo);
        log.info("Ferdigstilt oppgave med oppgaveId={}", hentOppgaveResponseTo.getId());

        if (JA.equals(request.getKVITTERING())) {
            try {
                sendKvittering(serviceklage, hentOppgaveResponseTo);
            } catch (Exception e) {
                log.warn("Kunne ikke produsere kvittering på mail", e);
            }
        }
    }

    private void sendKvittering(Serviceklage serviceklage, HentOppgaveResponseTo hentOppgaveResponseTo) throws JsonProcessingException {

        String email = oicdUtils.getEmailForIssuer(AZURE_ISSUER).orElseThrow(() -> new ClientErrorNotFoundException("Fant ikke email-adresse i token", ErrorCode.TOKEN_EMAIL_MISSING));
        log.info("Kvittering på innsendt klassifiseringsskjema sendes til epost: {}", email);

        LinkedHashMap<String, String> questionAnswerMap = createQuestionAnswerMap(serviceklage, hentOppgaveResponseTo);

        byte[] pdf = pdfService.opprettKlassifiseringPdf(questionAnswerMap);

        mailHelper.sendEmail(fromAddress,
                email,
                "Kvittering på innsendt klassifiseringsskjema",
                "Serviceklage med oppgave-id " + hentOppgaveResponseTo.getId() + " har blitt klassifisert. " +
                        "Innholdet i ditt utfylte skjema ligger vedlagt.",
                pdf);
        log.info("Kvittering sendt på mail til saksbehandler");
    }

    private LinkedHashMap<String, String> createQuestionAnswerMap(Serviceklage serviceklage, HentOppgaveResponseTo hentOppgaveResponseTo) throws JsonProcessingException {
        LinkedHashMap<String, String> questionAnswerMap = new LinkedHashMap<>();
        HentSkjemaResponse skjemaResponse = hentOppgaveResponseTo.getJournalpostId() != null ?
                hentSkjemaService.hentSkjema(hentOppgaveResponseTo.getJournalpostId()) :
                hentSkjemaService.readSkjema();
        Map<String, String> answersMap = new ObjectMapper().readValue(serviceklage.getKlassifiseringJson(), new TypeReference<Map<String, String>>() {
                })
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> !defaultValuesContainsEntry(skjemaResponse, entry))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        addEntriesToQuestionAnswerMap(answersMap, skjemaResponse.getQuestions(), questionAnswerMap);
        return questionAnswerMap;
    }

    private void addEntriesToQuestionAnswerMap(Map<String, String> answersMap, List<Question> questions, Map<String, String> questionAnswerMap) {
        for (Question q : questions) {
            String questionId = q.getId();
            if (answersMap.containsKey(q.getId()) && !questionAnswerMap.containsKey(q.getText())) {
                String question = getQuestionById(questions, questionId)
                        .orElseThrow(() -> new ServerErrorException("Finner ikke spørsmål med id=" + questionId))
                        .getText();
                questionAnswerMap.put(question, answersMap.get(questionId));
            }

            if (q.getType().equals(Question.TypeEnum.RADIO)) {
                Optional<Answer> answer = q.getAnswers().stream()
                        .filter(a -> a.getAnswer().equals(answersMap.get(questionId)))
                        .findFirst();

                if (answer.isPresent()
                        && answer.get().getQuestions() != null
                        && !answer.get().getQuestions().isEmpty()
                        && !NONE.equals(answer.get().getNext())) {
                    addEntriesToQuestionAnswerMap(answersMap, answer.get().getQuestions(), questionAnswerMap);
                }
            }
        }
    }

    private boolean defaultValuesContainsEntry(HentSkjemaResponse skjemaResponse, Map.Entry entry) {
        if (skjemaResponse.getDefaultAnswers() == null || skjemaResponse.getDefaultAnswers().getAnswers() == null) {
            return false;
        }
        return skjemaResponse.getDefaultAnswers().getAnswers().containsKey(entry.getKey().toString());
    }

    private void opprettSlettingOppgave(HentOppgaveResponseTo hentOppgaveResponseTo) {
        OpprettOppgaveRequestTo opprettOppgaveRequestTo = opprettOppgaveRequestToMapper.mapSlettingOppgave(hentOppgaveResponseTo);
        OpprettOppgaveResponseTo opprettOppgaveResponseTo = oppgaveConsumer.opprettOppgave(opprettOppgaveRequestTo);
        log.info("Opprettet oppgave med oppgaveId={}", opprettOppgaveResponseTo.getId());
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
        serviceklage.setBehandlesSomServiceklage(request.getBEHANDLESSOMSERVICEKLAGE());
        serviceklage.setBehandlesSomServiceklageUtdypning(request.getBEHANDLESSOMSERVICEKLAGEUTDYPNING());
        serviceklage.setFremmetDato(request.getFREMMETDATO() == null ? null : LocalDate.parse(request.getFREMMETDATO()));
        serviceklage.setInnsender(request.getINNSENDER());
        serviceklage.setKanal(request.getKANAL());
        serviceklage.setKanalUtdypning(request.getKANALUTDYPNING());
        serviceklage.setEnhetsnummerPaaklaget(extractEnhetsnummer(request.getENHETSNUMMERPAAKLAGET()));
        serviceklage.setEnhetsnummerBehandlende(JA.equals(request.getPAAKLAGETENHETERBEHANDLENDE()) ?
                extractEnhetsnummer(request.getENHETSNUMMERPAAKLAGET()) :
                extractEnhetsnummer(request.getENHETSNUMMERBEHANDLENDE()));
        serviceklage.setGjelder(request.getGJELDER());
        serviceklage.setBeskrivelse(request.getBESKRIVELSE());
        serviceklage.setYtelse(request.getYTELSE());
        serviceklage.setRelatert(request.getRELATERT());
        serviceklage.setTema(request.getTEMA());
        serviceklage.setTemaUtdypning(mapTemaUtdypning(request));
        serviceklage.setUtfall(request.getUTFALL());
        serviceklage.setAarsak(request.getAARSAK());
        serviceklage.setTiltak(request.getTILTAK());
        serviceklage.setSvarmetode(request.getSVARMETODE());
        serviceklage.setSvarmetodeUtdypning(mapSvarmetodeUtdypning(request));
        serviceklage.setAvsluttetDato(LocalDateTime.now());
        try {
            serviceklage.setKlassifiseringJson(new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Kan ikke konvertere klassifiseringsrequest til JSON-string", e);
        }
    }

    private void ferdigstillOppgave(HentOppgaveResponseTo hentOppgaveResponseTo) {
        EndreOppgaveRequestTo endreOppgaveRequestTo = endreOppgaveRequestToMapper.mapFerdigstillRequest(hentOppgaveResponseTo);
        oppgaveConsumer.endreOppgave(endreOppgaveRequestTo);
    }

    private String mapTemaUtdypning(KlassifiserServiceklageRequest request) {
        if (!isBlank(request.getVENTE())) {
            return request.getVENTE();
        } else if (!isBlank(request.getTILGJENGELIGHET())) {
            return request.getTILGJENGELIGHET();
        } else if (!isBlank(request.getINFORMASJON())) {
            return request.getINFORMASJON();
        } else if (!isBlank(request.getVEILEDNING())) {
            return request.getVEILEDNING();
        } else if (!isBlank(request.getTEMAUTDYPNING())) {
            return request.getTEMAUTDYPNING();
        } else {
            return null;
        }
    }

    private String mapSvarmetodeUtdypning(KlassifiserServiceklageRequest request) {
        if (!isBlank(request.getSVARIKKENOEDVENDIG())) {
            if (request.getSVARIKKENOEDVENDIG().equals(ANNET)) {
                return request.getSVARMETODEUTDYPNING();
            } else {
                return request.getSVARIKKENOEDVENDIG();
            }
        } else {
            return null;
        }
    }

    private String extractEnhetsnummer(String enhet) {
        if (isBlank(enhet)) {
            return null;
        }
        int l = enhet.trim().length();
        if (l >= 4) {
            String enhetsnummer = enhet.trim().substring(l - 4);
            if (isNumeric(enhetsnummer)) {
                return enhetsnummer;
            }
        }
        throw new ClientErrorException("Klarer ikke å hente ut enhetsnummer for enhet=" + enhet);
    }
}
