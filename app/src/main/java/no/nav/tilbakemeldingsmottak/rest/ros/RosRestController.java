package no.nav.tilbakemeldingsmottak.rest.ros;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.tilbakemeldingsmottak.api.RosRestControllerApi;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.SendRosRequest;
import no.nav.tilbakemeldingsmottak.model.SendRosResponse;
import no.nav.tilbakemeldingsmottak.rest.ros.service.RosService;
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

@Slf4j
@ProtectedWithClaims(issuer = "azuread")
@RestController
@RequiredArgsConstructor
public class RosRestController implements RosRestControllerApi {

    private final RosService rosService;
    private final SendRosValidator sendRosValidator;

    @Transactional
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "sendRos"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<SendRosResponse> sendRos(@RequestBody SendRosRequest request) throws SendEmailException {
        sendRosValidator.validateRequest(request);
        rosService.sendRos(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SendRosResponse.builder().message("Ros sendt").build());
    }
}
