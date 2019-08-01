package no.nav.tilbakemeldingsmottak.rest;

import no.nav.security.oidc.api.Protected;
import no.nav.security.oidc.api.Unprotected;
import no.nav.tilbakemeldingsmottak.api.SendRosRequest;
import no.nav.tilbakemeldingsmottak.api.SendRosResponse;
import no.nav.tilbakemeldingsmottak.service.RosService;
import no.nav.tilbakemeldingsmottak.validators.SendRosValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

@Protected
@RestController
@RequestMapping("/rest/ros")
public class RosRestController {

    private final RosService rosService;
    private final SendRosValidator sendRosValidator;


    @Inject
    public RosRestController(final RosService rosService) {
        this.rosService = rosService;
        this.sendRosValidator = new SendRosValidator();
    }

    @Transactional
    @PostMapping
    @Unprotected
    public ResponseEntity<SendRosResponse> sendRos(@RequestBody SendRosRequest request) throws MessagingException {
        sendRosValidator.validateRequest(request);
        rosService.sendRos(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SendRosResponse.builder().message("Ros sendt").build());
    }
}
