package no.nav.tilbakemeldingsmottak.rest;

import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.api.SendRosRequest;
import no.nav.tilbakemeldingsmottak.service.RosService;
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

    @Inject
    public RosRestController(final RosService rosService) {
        this.rosService = rosService;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> sendRos(@RequestBody SendRosRequest request) throws MessagingException {
        rosService.sendRos(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Ros sendt");
    }
}
