package no.nav.tilbakemeldingsmottak.rest.ros;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosRequest;
import no.nav.tilbakemeldingsmottak.rest.ros.domain.SendRosResponse;
import no.nav.tilbakemeldingsmottak.rest.ros.service.RosService;
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/ros")
@RequiredArgsConstructor
public class RosRestController {

    private final RosService rosService;
    private final SendRosValidator sendRosValidator;

    @Transactional
    @PostMapping
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "sendRos"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<SendRosResponse> sendRos(@RequestBody SendRosRequest request) throws MessagingException {
        sendRosValidator.validateRequest(request);
        rosService.sendRos(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SendRosResponse.builder().message("Ros sendt").build());
    }
}
