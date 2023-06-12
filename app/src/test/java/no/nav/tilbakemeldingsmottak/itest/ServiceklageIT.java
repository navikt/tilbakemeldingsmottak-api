package no.nav.tilbakemeldingsmottak.itest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import no.nav.tilbakemeldingsmottak.model.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageResponse;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.KlagetyperEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.PaaVegneAvEnum;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static no.nav.tilbakemeldingsmottak.TestUtils.AARSAK;
import static no.nav.tilbakemeldingsmottak.TestUtils.BEHANDLES_SOM_SERVICEKLAGE;
import static no.nav.tilbakemeldingsmottak.TestUtils.BESKRIVELSE;
import static no.nav.tilbakemeldingsmottak.TestUtils.FREMMET_DATO;
import static no.nav.tilbakemeldingsmottak.TestUtils.GJELDER;
import static no.nav.tilbakemeldingsmottak.TestUtils.INNSENDER;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETYPER;
import static no.nav.tilbakemeldingsmottak.TestUtils.KOMMUNAL_KLAGE;
import static no.nav.tilbakemeldingsmottak.TestUtils.NAV_ENHETSNR_1;
import static no.nav.tilbakemeldingsmottak.TestUtils.NAV_ENHETSNR_2;
import static no.nav.tilbakemeldingsmottak.TestUtils.ORGANISASJONSNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.RELATERT;
import static no.nav.tilbakemeldingsmottak.TestUtils.SAKSBEHANDLER;
import static no.nav.tilbakemeldingsmottak.TestUtils.TEMA;
import static no.nav.tilbakemeldingsmottak.TestUtils.TILTAK;
import static no.nav.tilbakemeldingsmottak.TestUtils.UTFALL;
import static no.nav.tilbakemeldingsmottak.TestUtils.VENTE;
import static no.nav.tilbakemeldingsmottak.TestUtils.YTELSE;
import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequestKommunalKlage;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER;
import static no.nav.tilbakemeldingsmottak.config.Constants.TOKENX_ISSUER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.INNMELDER_MANGLER_FULLMAKT_ANSWER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceklageIT extends ApplicationTest {

    private static final String URL_SENDINN_SERVICEKLAGE = "/rest/serviceklage";
    private static final String URL_BEHANDLE_SERVICEKLAGE = "/rest/taskserviceklage";
    private static final String KLASSIFISER = "klassifiser";
    private static final String HENT_SKJEMA = "hentskjema";
    private static final String HENT_DOKUMENT = "hentdokument";
    private static final String JOURNALPOST_ID = "12345";
    private static final String OPPGAVE_ID = "1234567";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void happyPathPrivatperson() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(msg, createHeaders(TOKENX_ISSUER, msg.getInnmelder().getPersonnummer(), true));

        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertTrue(serviceklage.getInnlogget());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.PRIVATPERSON.value);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    public void happyPathPrivatpersonIkkePaLogget() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();

        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer(), false));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertFalse(serviceklage.getInnlogget());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.PRIVATPERSON.value);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    void happyPathAnnenPerson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(TOKENX_ISSUER, request.getInnmelder().getPersonnummer(), true));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.ANNEN_PERSON.value);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }


    @Test
    void happyPathBedrift() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvBedrift();
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), ORGANISASJONSNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.BEDRIFT.value);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    void happyPathOenskerAaKontaktes() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setOenskerAaKontaktes(true);
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer(), true));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.PRIVATPERSON.value);
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNull(serviceklage.getSvarmetode());
        assertNull(serviceklage.getSvarmetodeUtdypning());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
    }

    @Test
    void happyPathInnsenderManglerFullmakt() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
        request.getInnmelder().setHarFullmakt(false);
        request.setOenskerAaKontaktes(null);
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.ANNEN_PERSON.value);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), INNMELDER_MANGLER_FULLMAKT_ANSWER);
    }

    @Test
    void happyPathFlereKlagetyper() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setKlagetyper(Arrays.asList(KlagetyperEnum.BREV, KlagetyperEnum.TELEFON));
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer(), true));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KlagetyperEnum.BREV.value + ", " + KlagetyperEnum.TELEFON.value);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvEnum.PRIVATPERSON.value);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    @SneakyThrows
    void happyPathOpprettJournalpostFeil() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/")).willReturn(aResponse().withStatus(500)));

        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceklageRepository.count(), 0);
    }

    @Test
    @SneakyThrows
    void happyPathOpprettOppgaveFeil() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPGAVE")).willReturn(aResponse().withStatus(500)));

        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceklageRepository.count(), 1);

    }

    @Test
    void shouldFailIfKlagetekstTooLarge() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setKlagetekst(RandomStringUtils.randomAlphabetic(50000));
        HttpEntity<OpprettServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @SneakyThrows
    void happyPathKlassifiserServiceklage() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, new HttpEntity<>(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        String fremmetDato = serviceklageRepository.findAll().iterator().next().getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFREMMETDATO(fremmetDato);
        HttpEntity<KlassifiserServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"));
        ResponseEntity<KlassifiserServiceklageResponse> response = restTemplate.exchange(URL_BEHANDLE_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + OPPGAVE_ID, HttpMethod.PUT, requestEntity, KlassifiserServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        assertEquals(serviceklageRepository.count(), 1);
        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();

        assertEquals(serviceklage.getBehandlesSomServiceklage(), BEHANDLES_SOM_SERVICEKLAGE);
        assertEquals(serviceklage.getFremmetDato().toString(), fremmetDato);
        assertEquals(serviceklage.getInnsender(), INNSENDER);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getEnhetsnummerPaaklaget(), NAV_ENHETSNR_1);
        assertEquals(serviceklage.getEnhetsnummerBehandlende(), NAV_ENHETSNR_2);
        assertEquals(serviceklage.getGjelder(), GJELDER);
        assertEquals(serviceklage.getBeskrivelse(), BESKRIVELSE);
        assertEquals(serviceklage.getRelatert(), RELATERT);
        assertEquals(serviceklage.getYtelse(), YTELSE);
        assertEquals(serviceklage.getTema(), TEMA);
        assertEquals(serviceklage.getTemaUtdypning(), VENTE);
        assertEquals(serviceklage.getUtfall(), UTFALL);
        assertEquals(serviceklage.getAarsak(), AARSAK);
        assertEquals(serviceklage.getTiltak(), TILTAK);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
        assertEquals(serviceklage.getKlassifiseringJson(), objectMapper.writeValueAsString(request));
    }

    @Test
    @SneakyThrows
    void dersomDetErKommunaltSkalDokumenterSlettes() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        ResponseEntity<OpprettServiceklageResponse> opprettServiceklageResponse = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, new HttpEntity<>(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);
        assertNotNull(opprettServiceklageResponse.getBody());

        Serviceklage opprettetServiceklage = serviceklageRepository.findByJournalpostId(opprettServiceklageResponse.getBody().getJournalpostId());
        String fremmetDato = opprettetServiceklage.getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequestKommunalKlage();
        request.setFREMMETDATO(fremmetDato);
        HttpEntity<KlassifiserServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering"));
        ResponseEntity<KlassifiserServiceklageResponse> response = restTemplate.exchange(URL_BEHANDLE_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + opprettServiceklageResponse.getBody().getOppgaveId(), HttpMethod.PUT, requestEntity, KlassifiserServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        verify(2, postRequestedFor(WireMock.urlPathMatching("/OPPGAVE")));

        assertEquals(serviceklageRepository.count(), 1);
        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();

        assertEquals(serviceklage.getBehandlesSomServiceklage(), KOMMUNAL_KLAGE);
        assertEquals(serviceklage.getKlassifiseringJson(), objectMapper.writeValueAsString(request));
    }

    @Test
    @SneakyThrows
    void enServiceklageSkalOpprettesOmDenMangler() {
        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        HttpEntity<KlassifiserServiceklageRequest> requestEntity = new HttpEntity<>(request, createHeaders(AZURE_ISSUER, request.getINNSENDER(), "serviceklage-klassifisering"));
        ResponseEntity<KlassifiserServiceklageResponse> response = restTemplate.exchange(URL_BEHANDLE_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + OPPGAVE_ID, HttpMethod.PUT, requestEntity, KlassifiserServiceklageResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getOpprettetDato());
        assertNull(serviceklage.getKlagetyper());
        assertNull(serviceklage.getKlagetekst());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);

        assertEquals(serviceklage.getBehandlesSomServiceklage(), BEHANDLES_SOM_SERVICEKLAGE);
        assertEquals(serviceklage.getFremmetDato().toString(), FREMMET_DATO);
        assertEquals(serviceklage.getInnsender(), INNSENDER);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getEnhetsnummerPaaklaget(), NAV_ENHETSNR_1);
        assertEquals(serviceklage.getEnhetsnummerBehandlende(), NAV_ENHETSNR_2);
        assertEquals(serviceklage.getGjelder(), GJELDER);
        assertEquals(serviceklage.getBeskrivelse(), BESKRIVELSE);
        assertEquals(serviceklage.getYtelse(), YTELSE);
        assertEquals(serviceklage.getTema(), TEMA);
        assertEquals(serviceklage.getTemaUtdypning(), VENTE);
        assertEquals(serviceklage.getUtfall(), UTFALL);
        assertEquals(serviceklage.getAarsak(), AARSAK);
        assertEquals(serviceklage.getTiltak(), TILTAK);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
        assertEquals(serviceklage.getKlassifiseringJson(), objectMapper.writeValueAsString(request));
    }

    @Test
    void happyPathHentSkjema() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, new HttpEntity<>(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(1, serviceklageRepository.count());
        String fremmetDato = serviceklageRepository.findAll().iterator().next().getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFREMMETDATO(fremmetDato);
        ResponseEntity<HentSkjemaResponse> response = restTemplate.exchange(URL_BEHANDLE_SERVICEKLAGE + "/" + HENT_SKJEMA + "/" + JOURNALPOST_ID, HttpMethod.GET, new HttpEntity<>(createHeaders(AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering")), HentSkjemaResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        HentSkjemaResponse hentSkjemaResponse = response.getBody();
        assertNotNull(hentSkjemaResponse);
        assertEquals(hentSkjemaResponse.getDefaultAnswers().getAnswers().get(ServiceklageConstants.FREMMET_DATO), fremmetDato);
        assertEquals(hentSkjemaResponse.getDefaultAnswers().getAnswers().get(ServiceklageConstants.INNSENDER), INNSENDER);
        assertEquals(hentSkjemaResponse.getDefaultAnswers().getAnswers().get(ServiceklageConstants.KANAL), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(hentSkjemaResponse.getDefaultAnswers().getAnswers().get(ServiceklageConstants.SVARMETODE), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(hentSkjemaResponse.getDefaultAnswers().getAnswers().get(ServiceklageConstants.SVAR_IKKE_NOEDVENDIG), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    @SneakyThrows
    void happyPathHentDokument() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        ResponseEntity<OpprettServiceklageResponse> opprettResponse = restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, new HttpEntity<>(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();

        String fremmetDato = serviceklage.getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFREMMETDATO(fremmetDato);
        assertNotNull(opprettResponse.getBody());
        ResponseEntity<HentDokumentResponse> response = restTemplate.exchange(URL_BEHANDLE_SERVICEKLAGE + "/" + HENT_DOKUMENT + "/" + opprettResponse.getBody().getOppgaveId(), HttpMethod.GET, new HttpEntity<>(createHeaders(AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering")), HentDokumentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    @SneakyThrows
    void journalpostManglerSkalKaste204() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        restTemplate.exchange(URL_SENDINN_SERVICEKLAGE, HttpMethod.POST, new HttpEntity<>(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();

        String fremmetDato = serviceklage.getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFREMMETDATO(fremmetDato);
        ResponseEntity<HentDokumentResponse> response = restTemplate.exchange(URL_BEHANDLE_SERVICEKLAGE + "/" + HENT_DOKUMENT + "/" + 99, HttpMethod.GET, new HttpEntity<>(createHeaders(AZURE_ISSUER, SAKSBEHANDLER, "serviceklage-klassifisering")), HentDokumentResponse.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}
