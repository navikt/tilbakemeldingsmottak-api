package no.nav.tilbakemeldingsmottak.rest.feilogmangler;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Protected;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerResponse;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.service.FeilOgManglerService;
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import no.nav.tilbakemeldingsmottak.api.FeilOgManglerRestControllerApi;

import javax.transaction.Transactional;

@Protected
@RestController
@Slf4j
@RequiredArgsConstructor
public class FeilOgManglerRestController implements FeilOgManglerRestControllerApi {

    private final FeilOgManglerService feilOgManglerService;
    private final MeldFeilOgManglerValidator meldFeilOgManglerValidator;

    @Transactional
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "meldFeilOgMangler"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<MeldFeilOgManglerResponse> meldFeilOgMangler(@RequestBody MeldFeilOgManglerRequest request) throws SendEmailException {
            meldFeilOgManglerValidator.validateRequest(request);
            feilOgManglerService.meldFeilOgMangler(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(MeldFeilOgManglerResponse.builder().message("Feil/mangel meldt").build());
    }
}
