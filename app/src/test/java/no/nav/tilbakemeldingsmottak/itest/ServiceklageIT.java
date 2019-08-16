package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.BEHANDLENDE_ENHET;
import static no.nav.tilbakemeldingsmottak.TestUtils.ER_SERVICEKLAGE;
import static no.nav.tilbakemeldingsmottak.TestUtils.GJELDER;
import static no.nav.tilbakemeldingsmottak.TestUtils.KANAL;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETYPE;
import static no.nav.tilbakemeldingsmottak.TestUtils.NEI_ANNET;
import static no.nav.tilbakemeldingsmottak.TestUtils.OENSKER_AA_KONTAKTES;
import static no.nav.tilbakemeldingsmottak.TestUtils.ORGANISASJONSNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.PAAKLAGET_ENHET;
import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.SVARMETODE;
import static no.nav.tilbakemeldingsmottak.TestUtils.TEMA;
import static no.nav.tilbakemeldingsmottak.TestUtils.UTFALL;
import static no.nav.tilbakemeldingsmottak.TestUtils.YTELSE_TJENESTE;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createRegistrerTilbakemeldingRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createRegistrerTilbakemeldingRequestNotServiceklage;
import static no.nav.tilbakemeldingsmottak.api.PaaVegneAvType.ANNEN_PERSON;
import static no.nav.tilbakemeldingsmottak.api.PaaVegneAvType.BEDRIFT;
import static no.nav.tilbakemeldingsmottak.api.PaaVegneAvType.PRIVATPERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingResponse;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ServiceklageIT extends AbstractIT {

    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";
    private static final String REGISTRER_TILBAKEMELDING = "registrerTilbakemelding";

    @Test
    void happyPathPrivatperson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), PRIVATPERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE.text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
    }

    @Test
    void happyPathAnnenPerson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), ANNEN_PERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE.text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
    }

    @Test
    void happyPathBedrift() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvBedrift();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<OpprettServiceklageResponse> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, OpprettServiceklageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), BEDRIFT.name());
        assertEquals(serviceklage.getKlagenGjelderId(), ORGANISASJONSNUMMER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE.text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
    }

    @Test
    void happyPathRegistrerTilbakemelding() {
        restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(createOpprettServiceklageRequestPrivatperson(), createHeaders()), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);

        RegistrerTilbakemeldingRequest request = createRegistrerTilbakemeldingRequest();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<RegistrerTilbakemeldingResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + JOURNALPOST_ID + "/" + REGISTRER_TILBAKEMELDING, HttpMethod.PUT, requestEntity, RegistrerTilbakemeldingResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertEquals(serviceklage.getErServiceklage(), ER_SERVICEKLAGE);
        assertEquals(serviceklage.getPaaklagetEnhet(), PAAKLAGET_ENHET);
        assertEquals(serviceklage.getKanal(), KANAL);
        assertEquals(serviceklage.getBehandlendeEnhet(), BEHANDLENDE_ENHET);
        assertEquals(serviceklage.getYtelseTjeneste(), YTELSE_TJENESTE);
        assertEquals(serviceklage.getTema(), TEMA);
        assertEquals(serviceklage.getUtfall(), UTFALL);
        assertEquals(serviceklage.getSvarmetode(), SVARMETODE.get(0));
    }

    @Test
    void happyPathRegistrerTilbakemeldingIkkeServiceklage() {
        restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(createOpprettServiceklageRequestPrivatperson(), createHeaders()), OpprettServiceklageResponse.class);

        assertEquals(serviceklageRepository.count(), 1);

        RegistrerTilbakemeldingRequest request = createRegistrerTilbakemeldingRequestNotServiceklage();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<RegistrerTilbakemeldingResponse> response = restTemplate.exchange(URL_SERVICEKLAGE + "/" + JOURNALPOST_ID + "/" + REGISTRER_TILBAKEMELDING, HttpMethod.PUT, requestEntity, RegistrerTilbakemeldingResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertEquals(serviceklage.getErServiceklage(), NEI_ANNET);
        assertEquals(serviceklage.getGjelder(), GJELDER);
    }

    @Test
    void happyPathHentServiceklage() {
        restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, new HttpEntity(createOpprettServiceklageRequestPrivatperson(), createHeaders()), OpprettServiceklageResponse.class);
        ResponseEntity<Serviceklage> response = restTemplate.getForEntity(URL_SERVICEKLAGE + "/" + JOURNALPOST_ID, Serviceklage.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = response.getBody();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), PRIVATPERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE.text);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
        assertEquals(serviceklage.getJournalpostId(), JOURNALPOST_ID);
    }
}