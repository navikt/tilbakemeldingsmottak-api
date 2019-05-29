package no.nav.serviceklagemottak.rest;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.api.RegistrerTilbakemeldingRequest;
import no.nav.serviceklagemottak.service.ServiceklageService;
import no.nav.serviceklagemottak.validators.OpprettServiceklageValidator;
import no.nav.serviceklagemottak.validators.RegistrerTilbakemeldingValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/rest/serviceklage")
public class ServiceklageRestController {

    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final ServiceklageService serviceklageService;
    private final RegistrerTilbakemeldingValidator registrerTilbakemeldingValidator;

    @Inject
    public ServiceklageRestController(final OpprettServiceklageValidator opprettServiceklageValidator,
                                      final ServiceklageService serviceklageService,
                                      final RegistrerTilbakemeldingValidator registrerTilbakemeldingValidator) {
        this.opprettServiceklageValidator = opprettServiceklageValidator;
        this.serviceklageService = serviceklageService;
        this.registrerTilbakemeldingValidator = registrerTilbakemeldingValidator;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<String> opprettServiceklage(@RequestBody OpprettServiceklageRequest request) {
        opprettServiceklageValidator.validateRequest(request);
        long id = serviceklageService.opprettServiceklage(request);
        return ResponseEntity.ok().body("Opprettet serviceklage med id=" + id);
    }

    @Transactional
    @PutMapping("/{id}/registrerTilbakemelding")
    public ResponseEntity<String> registrerTilbakemelding(@PathVariable long id,
                                                          @RequestBody RegistrerTilbakemeldingRequest request) {
        registrerTilbakemeldingValidator.validateRequest(request);
        serviceklageService.registrerTilbakemelding(id, request);
        return ResponseEntity.ok().body("Registrert tilbakemelding på serviceklage med id=" + id);
    }

}
