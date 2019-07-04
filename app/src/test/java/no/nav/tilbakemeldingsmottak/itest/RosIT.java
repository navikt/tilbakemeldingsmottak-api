package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.api.SendRosRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class RosIT extends AbstractIT {

    private static final String URL_ROS = "/rest/ros";

    @Test
    void happyPath() throws MessagingException, IOException {
        SendRosRequest request = createSendRosRequest();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<String> response = restTemplate.exchange(URL_ROS, HttpMethod.POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getBeskrivelse()));
        assertTrue(message.getContent().toString().contains(request.getNavKontor()));
        assertTrue(message.getContent().toString().contains(request.getNavn()));
    }
}
