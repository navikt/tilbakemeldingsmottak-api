package no.nav.tilbakemeldingsmottak.itest;

import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static no.nav.tilbakemeldingsmottak.TestUtils.createMeldFeilOgManglerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeilOgManglerIT extends ApplicationTest {

    private static final String URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler";

    @Test
    void happyPath() throws MessagingException, IOException {
        MeldFeilOgManglerRequest request = createMeldFeilOgManglerRequest();
        HttpEntity<MeldFeilOgManglerRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<MeldFeilOgManglerResponse> response = restTemplate.exchange(URL_FEIL_OG_MANGLER, HttpMethod.POST, requestEntity, MeldFeilOgManglerResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

/*
        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getEpost()));
        assertTrue(message.getContent().toString().contains(request.getFeiltype().text));
        assertTrue(message.getContent().toString().contains(request.getMelding()));
*/
    }
}
