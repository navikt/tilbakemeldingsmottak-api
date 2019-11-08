package no.nav.tilbakemeldingsmottak.rest.feilogmangler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.domain.MeldFeilOgManglerResponse;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.service.FeilOgManglerService;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

@Protected
@RestController
@RequestMapping("/rest/feil-og-mangler")
@Slf4j
@RequiredArgsConstructor
public class FeilOgManglerRestController {

    private final FeilOgManglerService feilOgManglerService;
    private final MeldFeilOgManglerValidator meldFeilOgManglerValidator;

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
                    .getMessage(), e);
            throw e;
        }
    }
}
