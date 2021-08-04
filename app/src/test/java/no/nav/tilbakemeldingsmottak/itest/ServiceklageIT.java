package no.nav.tilbakemeldingsmottak.itest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static no.nav.tilbakemeldingsmottak.TestUtils.*;
import static no.nav.tilbakemeldingsmottak.config.Constants.*;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNMELDER_MANGLER_FULLMAKT_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService.SUBJECT_JOURNALPOST_FEILET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService.SUBJECT_OPPGAVE_FEILET;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;

import org.springframework.test.context.transaction.TestTransaction;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import java.util.Arrays;

class ServiceklageIT extends ApplicationTest {

    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";
    private static final String KLASSIFISER = "klassifiser";
    private static final String HENT_SKJEMA = "hentskjema";
    private static final String HENT_DOKUMENT = "hentdokument";
    private static final String JOURNALPOST_ID ="12345";
    private static final String OPPGAVE_ID = "12345678";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void happyPathPrivatperson() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        HttpEntity requestEntity = new HttpEntity(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertTrue(serviceklage.getInnlogget());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.PRIVATPERSON.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    public void happyPathPrivatpersonIkkePaLogget() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();

        HttpEntity requestEntity = new HttpEntity(msg, createHeaders(RESTSTS_ISSUER, "srvtilbakemeldings"));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertTrue(!serviceklage.getInnlogget());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.PRIVATPERSON.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

   @Test
    void happyPathAnnenPerson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.ANNEN_PERSON.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }


    @Test
    void happyPathBedrift() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvBedrift();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), ORGANISASJONSNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.BEDRIFT.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    void happyPathOenskerAaKontaktes() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setOenskerAaKontaktes(true);
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.PRIVATPERSON.text);
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getSvarmetode(), null);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), null);
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
    }

    @Test
    void happyPathInnsenderManglerFullmakt() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
        request.getInnmelder().setHarFullmakt(false);
        request.setOenskerAaKontaktes(null);
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), KLAGETYPER.get(0).text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.ANNEN_PERSON.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), INNMELDER_MANGLER_FULLMAKT_ANSWER);
    }

    @Test
    void happyPathFlereKlagetyper() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setKlagetyper(Arrays.asList(Klagetype.BREV, Klagetype.TELEFON));
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
        assertNotNull(serviceklage.getOpprettetDato());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetyper(), Klagetype.BREV.text + ", " + Klagetype.TELEFON.text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertNotNull(serviceklage.getFremmetDato());
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.PRIVATPERSON.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    @SneakyThrows
    void happyPathOpprettJournalpostFeil() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/")).willReturn(aResponse().withStatus(500)));

        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceklageRepository.count(), 0);

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertEquals(message.getSubject(), SUBJECT_JOURNALPOST_FEILET);
        assertEquals(message.getSender().toString(), "srvtilbakemeldings@preprod.local");
        assertEquals(message.getRecipients(Message.RecipientType.TO)[0].toString(), "nav.serviceklager@preprod.local");

        assertEquals("Feil ved opprettelse av journalpost, klage videresendt til " + message.getRecipients(Message.RecipientType.TO)[0], response.getBody().getMessage());
    }

    @Test
    @SneakyThrows
    void happyPathOpprettOppgaveFeil() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/OPPGAVE")).willReturn(aResponse().withStatus(500)));

        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(LOGINSERVICE_ISSUER, SAKSBEHANDLER));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceklageRepository.count(), 1);

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertEquals(message.getSubject(), SUBJECT_OPPGAVE_FEILET);
        assertEquals(message.getSender().toString(), "srvtilbakemeldings@preprod.local");
        assertEquals(message.getRecipients(Message.RecipientType.TO)[0].toString(), "nav.serviceklager@preprod.local");

        assertEquals("Feil ved opprettelse av oppgave, journalpostId videresendt til " + message.getRecipients(Message.RecipientType.TO)[0], response.getBody().getMessage());
    }

    @Test
    void shouldFailIfKlagetekstTooLarge() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setKlagetekst(RandomStringUtils.randomAlphabetic(50000));
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(AZURE_ISSUER, request.getInnmelder().getPersonnummer()));
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @SneakyThrows
    void happyPathKlassifiserServiceklage() {
        OpprettServiceklageRequest msg = createOpprettServiceklageRequestPrivatperson();
        restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        String fremmetDato = serviceklageRepository.findAll().iterator().next().getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFremmetDato(fremmetDato);
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(LOGINSERVICE_ISSUER, SAKSBEHANDLER));
        ResponseEntity<KlassifiserServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + OPPGAVE_ID, HttpMethod.PUT, requestEntity, KlassifiserServiceklageResponse.class);

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
        ResponseEntity<OpprettServiceklageResponse> opprettServiceklageResponse = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        Serviceklage opprettetServiceklage = serviceklageRepository.findByJournalpostId(opprettServiceklageResponse.getBody().getJournalpostId());
        String fremmetDato = opprettetServiceklage.getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequestKommunalKlage();
        request.setFremmetDato(fremmetDato);
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<KlassifiserServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + opprettServiceklageResponse.getBody().getOppgaveId(), HttpMethod.PUT, requestEntity, KlassifiserServiceklageResponse.class);

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
        HttpEntity requestEntity = new HttpEntity(request, createHeaders(LOGINSERVICE_ISSUER, request.getInnsender()));
        ResponseEntity<KlassifiserServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + KLASSIFISER + "?oppgaveId=" + OPPGAVE_ID, HttpMethod.PUT, requestEntity, KlassifiserServiceklageResponse.class);
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
        restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        String fremmetDato = serviceklageRepository.findAll().iterator().next().getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFremmetDato(fremmetDato);
        ResponseEntity<HentSkjemaResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + HENT_SKJEMA + "/" + JOURNALPOST_ID, HttpMethod.GET, new HttpEntity<>(createHeaders(LOGINSERVICE_ISSUER, SAKSBEHANDLER)), HentSkjemaResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        HentSkjemaResponse hentSkjemaResponse = response.getBody();
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
        ResponseEntity<OpprettServiceklageResponse> opprettResponse = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(msg, createHeaders(AZURE_ISSUER, msg.getInnmelder().getPersonnummer())), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();

        String fremmetDato = serviceklage.getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.setFremmetDato(fremmetDato);
        ResponseEntity<HentDokumentResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + HENT_DOKUMENT + "/" + opprettResponse.getBody().getOppgaveId(), HttpMethod.GET, new HttpEntity<>(createHeaders(LOGINSERVICE_ISSUER, SAKSBEHANDLER)), HentDokumentResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}