package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.BEHANDLES_SOM_SERVICEKLAGE;
import static no.nav.tilbakemeldingsmottak.TestUtils.ENHETSNUMMER_BEHANDLENDE;
import static no.nav.tilbakemeldingsmottak.TestUtils.ENHETSNUMMER_PAAKLAGET;
import static no.nav.tilbakemeldingsmottak.TestUtils.FREMMET_DATO;
import static no.nav.tilbakemeldingsmottak.TestUtils.GJELDER;
import static no.nav.tilbakemeldingsmottak.TestUtils.INNSENDER;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETYPER;
import static no.nav.tilbakemeldingsmottak.TestUtils.ORGANISASJONSNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.TEMA;
import static no.nav.tilbakemeldingsmottak.TestUtils.UTFALL;
import static no.nav.tilbakemeldingsmottak.TestUtils.YTELSE;
import static no.nav.tilbakemeldingsmottak.TestUtils.createKlassifiserServiceklageRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatpersonLokaltKontor;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BRUKER_IKKE_BEDT_OM_SVAR_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNMELDER_MANGLER_FULLMAKT_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_SERVICEKLAGESKJEMA_ANSWER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.SneakyThrows;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Klagetype;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.PaaVegneAvType;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.TestTransaction;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;

class ServiceklageIT extends AbstractIT {

    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";
    private static final String KLASSIFISER = "klassifiser";

    @Test
    void happyPathPrivatperson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
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
        assertEquals(serviceklage.getInnsender(), PaaVegneAvType.PRIVATPERSON.text);
        assertEquals(serviceklage.getKanal(), KANAL_SERVICEKLAGESKJEMA_ANSWER);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    void happyPathAnnenPerson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
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
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
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
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
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
    void happyPathLokaltKontor() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatpersonLokaltKontor();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceklageRepository.count(), 0);

        MimeMessage message = smtpServer.getReceivedMessages()[0];
    }

    @Test
    void happyPathKlassifiserServiceklage() {
        restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(createOpprettServiceklageRequestPrivatperson(), createHeaders()), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);
        String fremmetDato = serviceklageRepository.findAll().iterator().next().getFremmetDato().toString();

        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        request.getAnswers().setFremmetDato(fremmetDato);
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
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
        assertEquals(serviceklage.getEnhetsnummerPaaklaget(), ENHETSNUMMER_PAAKLAGET);
        assertEquals(serviceklage.getEnhetsnummerBehandlende(), ENHETSNUMMER_BEHANDLENDE);
        assertEquals(serviceklage.getGjelder(), GJELDER);
        assertEquals(serviceklage.getYtelse(), YTELSE);
        assertEquals(serviceklage.getTema(), TEMA);
        assertEquals(serviceklage.getUtfall(), UTFALL);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    void shouldCreateServiceklageIfServiceklageNotFound() {
        KlassifiserServiceklageRequest request = createKlassifiserServiceklageRequest();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
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
        assertEquals(serviceklage.getEnhetsnummerPaaklaget(), ENHETSNUMMER_PAAKLAGET);
        assertEquals(serviceklage.getEnhetsnummerBehandlende(), ENHETSNUMMER_BEHANDLENDE);
        assertEquals(serviceklage.getGjelder(), GJELDER);
        assertEquals(serviceklage.getYtelse(), YTELSE);
        assertEquals(serviceklage.getTema(), TEMA);
        assertEquals(serviceklage.getUtfall(), UTFALL);
        assertEquals(serviceklage.getSvarmetode(), SVAR_IKKE_NOEDVENDIG_ANSWER);
        assertEquals(serviceklage.getSvarmetodeUtdypning(), BRUKER_IKKE_BEDT_OM_SVAR_ANSWER);
    }

    @Test
    void shouldFailIfKlagetekstTooLarge() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        request.setKlagetekst(RandomStringUtils.randomAlphabetic(50000));
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}