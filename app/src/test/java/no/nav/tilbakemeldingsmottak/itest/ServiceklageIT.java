package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.TestUtils.KLAGETYPE;
import static no.nav.tilbakemeldingsmottak.TestUtils.OENSKER_AA_KONTAKTES;
import static no.nav.tilbakemeldingsmottak.TestUtils.ORGANISASJONSNUMMER;
import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER_INNMELDER;
import static no.nav.tilbakemeldingsmottak.TestUtils.PERSONNUMMER_PERSON;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.tilbakemeldingsmottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.tilbakemeldingsmottak.api.PaaVegneAvType.ANNEN_PERSON;
import static no.nav.tilbakemeldingsmottak.api.PaaVegneAvType.BEDRIFT;
import static no.nav.tilbakemeldingsmottak.api.PaaVegneAvType.PRIVATPERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ServiceklageIT extends AbstractIT {

    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";

    @Test
    void happyPathPrivatperson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPrivatperson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), PRIVATPERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER_INNMELDER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
    }

    @Test
    void happyPathAnnenPerson() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvPerson();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), ANNEN_PERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId(), PERSONNUMMER_PERSON);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
    }

    @Test
    void happyPathBedrift() {
        OpprettServiceklageRequest request = createOpprettServiceklageRequestPaaVegneAvBedrift();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), BEDRIFT.name());
        assertEquals(serviceklage.getKlagenGjelderId(), ORGANISASJONSNUMMER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
    }

    @Test
    void happyPathRegistrerTilbakemelding() {

    }
}