package no.nav.tilbakemeldingsmottak.rest;

import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.security.oidc.api.Unprotected;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingRequest;
import no.nav.tilbakemeldingsmottak.api.RegistrerTilbakemeldingResponse;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.service.ServiceklageService;
import no.nav.tilbakemeldingsmottak.validators.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.validators.RegistrerTilbakemeldingValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.FileNotFoundException;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/serviceklage")
public class ServiceklageRestController {

    private final ServiceklageService serviceklageService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final RegistrerTilbakemeldingValidator registrerTilbakemeldingValidator;

    @Inject
    public ServiceklageRestController(final ServiceklageService serviceklageService) {
        this.serviceklageService = serviceklageService;
        this.opprettServiceklageValidator = new OpprettServiceklageValidator();
        this.registrerTilbakemeldingValidator = new RegistrerTilbakemeldingValidator();
    }

    @Transactional
    @PostMapping
    @Unprotected
    public ResponseEntity<OpprettServiceklageResponse> opprettServiceklage(@RequestBody OpprettServiceklageRequest request) throws FileNotFoundException, DocumentException {
        try {
            opprettServiceklageValidator.validateRequest(request);
            long id = serviceklageService.opprettServiceklage(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(OpprettServiceklageResponse.builder().message("Opprettet serviceklage med serviceklageId=" + id).build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("opprettServiceklage feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("opprettServiceklage feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }

    @Transactional
    @PutMapping(value = "/{journalpostId}/registrerTilbakemelding")
    @Unprotected
    public ResponseEntity<RegistrerTilbakemeldingResponse> registrerTilbakemelding(@RequestBody RegistrerTilbakemeldingRequest request, @PathVariable String journalpostId) {
        try {
            registrerTilbakemeldingValidator.validateRequest(request);
            serviceklageService.registrerTilbakemelding(request, journalpostId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(RegistrerTilbakemeldingResponse.builder().message("Registrert tilbakemelding på serviceklage med journalpostId=" + journalpostId).build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("registrerTilbakemelding feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("registrerTilbakemelding feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }

    @Transactional
    @GetMapping(value = "/{journalpostId}")
    @Unprotected
    public ResponseEntity<Serviceklage> hentServiceklage(@PathVariable String journalpostId) {
        try {
            Serviceklage response = serviceklageService.hentServiceklage(journalpostId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("hentServiceklage feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("hentServiceklage feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }

    @GetMapping("/hello-world-protected")
    public ResponseEntity<String> helloWorldProtected() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello world");
    }
}
