package no.nav.serviceklagemottak.itest;

import static no.nav.serviceklagemottak.TestUtils.createOpprettServiceklageRequestPrivatperson;
import static org.junit.jupiter.api.Assertions.assertEquals;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ServiceklageIT extends AbstractIT {

    private static final String URL_SERVICEKLAGE = "/rest/serviceklage";

    @Test
    void happyPath() throws IOException {
        OpprettServiceklageRequest opprettServiceklageRequest = createOpprettServiceklageRequestPrivatperson();
        HttpEntity request = new HttpEntity(opprettServiceklageRequest, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_SERVICEKLAGE, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
}
