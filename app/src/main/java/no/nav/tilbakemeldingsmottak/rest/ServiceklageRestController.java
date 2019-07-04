package no.nav.tilbakemeldingsmottak.rest;

import com.itextpdf.text.DocumentException;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.service.ServiceklageService;
import no.nav.tilbakemeldingsmottak.validators.OpprettServiceklageValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.FileNotFoundException;

@Protected
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
    public ResponseEntity<String> opprettServiceklage(@RequestBody OpprettServiceklageRequest request) throws FileNotFoundException, DocumentException {
        opprettServiceklageValidator.validateRequest(request);
        long id = serviceklageService.opprettServiceklage(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Opprettet serviceklage med serviceaklageId=" + id);
    }

    @GetMapping(value = "/ping")
    public String pong(){
        return "pong" + Math.random();
    }
}
