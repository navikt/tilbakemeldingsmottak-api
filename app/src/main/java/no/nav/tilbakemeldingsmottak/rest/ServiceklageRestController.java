package no.nav.tilbakemeldingsmottak.rest;

import com.itextpdf.text.DocumentException;
import no.nav.security.oidc.api.Protected;
import no.nav.security.oidc.api.Unprotected;
import no.nav.tilbakemeldingsmottak.api.HentServiceklagerResponse;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.service.ServiceklageService;
import no.nav.tilbakemeldingsmottak.validators.OpprettServiceklageValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<String> opprettServiceklage(@RequestBody OpprettServiceklageRequest request,
                                                      @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader) throws FileNotFoundException, DocumentException {
        opprettServiceklageValidator.validateRequest(request);
        long id = serviceklageService.opprettServiceklage(request, authorizationHeader);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Opprettet serviceklage med serviceklageId=" + id);
    }

    @GetMapping(value = "/ping")
    @Unprotected
    public String pong(){
        return "pong" + Math.random();
    }

    @Transactional
    @PutMapping(value = "/{serviceklageId}/registrerTilbakemelding")
    @Unprotected
    public ResponseEntity<String> sendTilbakemelding(@RequestBody RegistrerTilbakemeldingRequest request, @PathVariable String serviceklageId) {
        serviceklageService.registrerTilbakemelding(request, serviceklageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Registrert tilbakemelding på serviceklage med serviceklageid=" + serviceklageId);
    }

    @Transactional
    @GetMapping(value = "/{brukerId}")
    @Unprotected
    public ResponseEntity<HentServiceklagerResponse> hentServiceklager(@PathVariable String brukerId) {
        HentServiceklagerResponse response = serviceklageService.hentServiceklager(brukerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
