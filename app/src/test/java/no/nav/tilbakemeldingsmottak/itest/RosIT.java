package no.nav.tilbakemeldingsmottak.itest;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequestWithNavKontor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosResponse;
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
        ResponseEntity<SendRosResponse> response = restTemplate.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getNavn()));
        assertTrue(message.getContent().toString().contains(request.getHvemRoses().text));
        assertTrue(message.getContent().toString().contains(request.getMelding()));
    }

    @Test
    void happyPathWithoutNavn() throws MessagingException, IOException {
        SendRosRequest request = createSendRosRequest();
        request.setNavn(null);
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<SendRosResponse> response = restTemplate.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getHvemRoses().text));
        assertTrue(message.getContent().toString().contains(request.getMelding()));
    }

    @Test
    void happyPathNavKontor() throws MessagingException, IOException {
        SendRosRequest request = createSendRosRequestWithNavKontor();
        HttpEntity requestEntity = new HttpEntity(request, createHeaders());
        ResponseEntity<SendRosResponse> response = restTemplate.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        MimeMessage message = smtpServer.getReceivedMessages()[0];
        assertTrue(message.getContent().toString().contains(request.getNavn()));
        assertTrue(message.getContent().toString().contains(request.getHvemRoses().text));
        assertTrue(message.getContent().toString().contains(request.getNavKontor()));
        assertTrue(message.getContent().toString().contains(request.getMelding()));
    }
}
