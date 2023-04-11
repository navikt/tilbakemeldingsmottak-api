package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import no.nav.tilbakemeldingsmottak.api.ServiceklageRestControllerApi;

import javax.transaction.Transactional;

@Slf4j
@ProtectedWithClaims(issuer = "azuread")
@RestController
@RequiredArgsConstructor
public class ServiceklageRestController implements ServiceklageRestControllerApi {

    private final OpprettServiceklageService opprettServiceklageService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final OidcUtils oidcUtils;

    @Transactional(dontRollbackOn = EksterntKallException.class)
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "opprettServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<OpprettServiceklageResponse>
            opprettServiceklage(@RequestBody OpprettServiceklageRequest request,
            @CookieValue(name = "selvbetjening-idtoken", required = false) String selvbetjening)
            {

        log.info("Mottatt serviceklage via skjema på nav.no");
        String paloggetBruker = oidcUtils.getSubject(selvbetjening);
        boolean innlogget = paloggetBruker != null;
        log.info("Bruker er innlogget " + innlogget);
        opprettServiceklageValidator.validateRequest(request, paloggetBruker);
        OpprettServiceklageResponse opprettServiceklageResponse = opprettServiceklageService.opprettServiceklage(request, innlogget);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(opprettServiceklageResponse);

    }

}
