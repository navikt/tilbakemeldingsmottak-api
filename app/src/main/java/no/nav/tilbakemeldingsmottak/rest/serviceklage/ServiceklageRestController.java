package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.nav.security.token.support.core.api.Protected;
import no.nav.tilbakemeldingsmottak.api.ServiceklageRestControllerApi;
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static no.nav.tilbakemeldingsmottak.config.Constants.TOKENX_ISSUER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.slf4j.LoggerFactory.getLogger;

@Protected
@RestController
@RequiredArgsConstructor
public class ServiceklageRestController implements ServiceklageRestControllerApi {

    private static final Logger log = getLogger(ServiceklageRestController.class);

    private final OpprettServiceklageService opprettServiceklageService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final OidcUtils oidcUtils;

    @Transactional(dontRollbackOn = EksterntKallException.class)
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "opprettServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<OpprettServiceklageResponse>
    opprettServiceklage(@RequestBody OpprettServiceklageRequest request) {

        log.info("Mottatt serviceklage via skjema på nav.no");
        Optional<String> paloggetBruker = oidcUtils.getPidForIssuer(TOKENX_ISSUER);

        boolean innlogget = paloggetBruker.isPresent();
        log.info("Bruker er innlogget " + innlogget);

        opprettServiceklageValidator.validateRequest(request, paloggetBruker);

        OpprettServiceklageResponse opprettServiceklageResponse = opprettServiceklageService.opprettServiceklage(request, innlogget);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(opprettServiceklageResponse);

    }

}
