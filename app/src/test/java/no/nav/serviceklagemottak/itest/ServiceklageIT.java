package no.nav.serviceklagemottak.itest;

import static no.nav.serviceklagemottak.TestUtils.KLAGETEKST;
import static no.nav.serviceklagemottak.TestUtils.KLAGETYPE;
import static no.nav.serviceklagemottak.TestUtils.OENSKER_AA_KONTAKTES;
import static no.nav.serviceklagemottak.TestUtils.ORGANISASJONSNUMMER;
import static no.nav.serviceklagemottak.TestUtils.PERSONNUMMER_INNMELDER;
import static no.nav.serviceklagemottak.TestUtils.PERSONNUMMER_PERSON;
import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvBedrift;
import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPaaVegneAvPerson;
import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static no.nav.serviceklagemottak.api.PaaVegneAvType.ANNEN_PERSON;
import static no.nav.serviceklagemottak.api.PaaVegneAvType.BEDRIFT;
import static no.nav.serviceklagemottak.api.PaaVegneAvType.PRIVATPERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.domain.Serviceklage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ServiceklageIT extends AbstractIT {

    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";

    @Test
    void happyPathPrivatperson() {
        OpprettServiceklageRequest opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        HttpEntity request = new HttpEntity(opprettServiceklageRequest, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), PRIVATPERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId().toString(), PERSONNUMMER_INNMELDER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
    }

    @Test
    void happyPathAnnenPerson() {
        OpprettServiceklageRequest opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvPerson();
        HttpEntity request = new HttpEntity(opprettServiceklageRequest, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), ANNEN_PERSON.name());
        assertEquals(serviceklage.getKlagenGjelderId().toString(), PERSONNUMMER_PERSON);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
    }

    @Test
    void happyPathBedrift() {
        OpprettServiceklageRequest opprettServiceklageRequest = createOpprettServiceklageRequestPaaVegneAvBedrift();
        HttpEntity request = new HttpEntity(opprettServiceklageRequest, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Serviceklage serviceklage = serviceklageRepository.findAll().iterator().next();
        assertNotNull(serviceklage.getServiceklageId());
        assertNotNull(serviceklage.getDatoOpprettet());
        assertEquals(serviceklage.getPaaVegneAv(), BEDRIFT.name());
        assertEquals(serviceklage.getKlagenGjelderId().toString(), ORGANISASJONSNUMMER);
        assertEquals(serviceklage.getKlagetype(), KLAGETYPE);
        assertEquals(serviceklage.getKlagetekst(), KLAGETEKST);
        assertEquals(serviceklage.getOenskerAaKontaktes(), OENSKER_AA_KONTAKTES);
    }
}