package no.nav.tilbakemeldingsmottak.itest;

import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static no.nav.tilbakemeldingsmottak.TestUtils.createBestillSamtaleRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BestillingAvSamtaleIT extends ApplicationTest {

    private static final String URL_BESTILLING_AV_SAMTALE = "/rest/bestilling-av-samtale";

    @Test
    void happyPath()  {
        BestillSamtaleRequest request = createBestillSamtaleRequest();
        HttpEntity<BestillSamtaleRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<BestillSamtaleResponse> response = restTemplate.exchange(URL_BESTILLING_AV_SAMTALE, HttpMethod.POST, requestEntity, BestillSamtaleResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
