package no.nav.tilbakemeldingsmottak.itest;

import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequest;
import static no.nav.tilbakemeldingsmottak.TestUtils.createSendRosRequestWithNavKontor;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RosIT extends ApplicationTest {

    private static final String URL_ROS = "/rest/ros";

    @Test
    void happyPath() {
        SendRosRequest request = createSendRosRequest();
        HttpEntity<SendRosRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<SendRosResponse> response = restTemplate.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void happyPathNavKontor() {
        SendRosRequest request = createSendRosRequestWithNavKontor();
        HttpEntity<SendRosRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<SendRosResponse> response = restTemplate.exchange(URL_ROS, HttpMethod.POST, requestEntity, SendRosResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
}
