package no.nav.serviceklagemottak.rest;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.service.ServiceklageService;
import no.nav.serviceklagemottak.validators.OpprettServiceklageValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/rest/serviceklage")
public class ServiceklageRestController {

    private final ServiceklageService serviceklageService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;

    @Inject
    public ServiceklageRestController(final ServiceklageService serviceklageService) {
        this.serviceklageService = serviceklageService;
        this.opprettServiceklageValidator = new OpprettServiceklageValidator();
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> opprettServiceklage(@RequestBody OpprettServiceklageRequest request) throws MessagingException {
        opprettServiceklageValidator.validateRequest(request);
        long id = serviceklageService.opprettServiceklage(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Opprettet serviceklage med serviceaklageId=" + id);
    }

}
