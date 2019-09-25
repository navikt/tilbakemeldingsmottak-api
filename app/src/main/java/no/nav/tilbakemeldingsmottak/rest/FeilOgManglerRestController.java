package no.nav.tilbakemeldingsmottak.rest;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerResponse;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.service.FeilOgManglerService;
import no.nav.tilbakemeldingsmottak.validators.MeldFeilOgManglerValidator;
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
@RequestMapping("/rest/feil-og-mangler")
@Slf4j
public class FeilOgManglerRestController {

    private final FeilOgManglerService feilOgManglerService;
    private final MeldFeilOgManglerValidator meldFeilOgManglerValidator;

    @Inject
    public FeilOgManglerRestController(final FeilOgManglerService feilOgManglerService) {
        this.feilOgManglerService = feilOgManglerService;
        this.meldFeilOgManglerValidator = new MeldFeilOgManglerValidator();
    }

    @Transactional
    @PostMapping
    public ResponseEntity<MeldFeilOgManglerResponse> meldFeilOgMangler(@RequestBody MeldFeilOgManglerRequest request) throws MessagingException {
        try {
            meldFeilOgManglerValidator.validateRequest(request);
            feilOgManglerService.meldFeilOgMangler(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MeldFeilOgManglerResponse.builder().message("Feil/mangel meldt").build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("meldFeilOgMangler feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("meldFeilOgMangler feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }
}
