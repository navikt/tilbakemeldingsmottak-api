package no.nav.tilbakemeldingsmottak.itest;

import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static no.nav.tilbakemeldingsmottak.TestUtils.createBestillSamtaleRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BestillingAvSamtaleIT extends ApplicationTest {

    private static final String URL_BESTILLING_AV_SAMTALE = "/rest/bestilling-av-samtale";

    @Test
    void happyPath() throws MessagingException, IOException {
        BestillSamtaleRequest request = createBestillSamtaleRequest();
        HttpEntity<BestillSamtaleRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<BestillSamtaleResponse> response = restTemplate.exchange(URL_BESTILLING_AV_SAMTALE, HttpMethod.POST, requestEntity, BestillSamtaleResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getFornavn()));
        assertTrue(message.getContent().toString().contains(request.getEtternavn()));
        assertTrue(message.getContent().toString().contains(request.getTelefonnummer()));
        assertTrue(message.getContent().toString().contains(request.getTidsrom().text));
    }
}
