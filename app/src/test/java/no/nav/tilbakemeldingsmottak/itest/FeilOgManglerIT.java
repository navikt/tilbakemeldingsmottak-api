package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.createMeldFeilOgManglerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class FeilOgManglerIT extends AbstractIT {

    private static final String URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler";

    @Test
    void happyPath() throws MessagingException, IOException {
        MeldFeilOgManglerRequest request = createMeldFeilOgManglerRequest();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_FEIL_OG_MANGLER, HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getNavn()));
        assertTrue(message.getContent().toString().contains(request.getTelefonnummer()));
        assertTrue(message.getContent().toString().contains(request.getFeiltype()));
        assertTrue(message.getContent().toString().contains(request.getMelding()));
    }
}
