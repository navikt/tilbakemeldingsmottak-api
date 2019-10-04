package no.nav.tilbakemeldingsmottak.rest.ros;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
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

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

@Slf4j
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
    public ResponseEntity<SendRosResponse> sendRos(@RequestBody SendRosRequest request) throws MessagingException {
        try {
            sendRosValidator.validateRequest(request);
            rosService.sendRos(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(SendRosResponse.builder().message("Ros sendt").build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("sendRos feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("sendRos feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }
}
