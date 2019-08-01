package no.nav.tilbakemeldingsmottak.rest;

import no.nav.security.oidc.api.Protected;
import no.nav.security.oidc.api.Unprotected;
import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.api.MeldFeilOgManglerResponse;
import no.nav.tilbakemeldingsmottak.service.FeilOgManglerService;
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
public class FeilOgManglerRestController {

    private final FeilOgManglerService feilOgManglerService;

    @Inject
    public FeilOgManglerRestController(final FeilOgManglerService feilOgManglerService) {
        this.feilOgManglerService = feilOgManglerService;
    }

    @Transactional
    @PostMapping
    @Unprotected
    public ResponseEntity<MeldFeilOgManglerResponse> meldFeilOgMangler(@RequestBody MeldFeilOgManglerRequest request) throws MessagingException {
        feilOgManglerService.meldFeilOgMangler(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MeldFeilOgManglerResponse.builder().message("Feil/mangel meldt").build());
    }
}
