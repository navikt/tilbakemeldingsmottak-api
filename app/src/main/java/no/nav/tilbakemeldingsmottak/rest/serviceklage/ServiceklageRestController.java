package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Protected;
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/serviceklage")
@RequiredArgsConstructor
public class ServiceklageRestController {

    private final OpprettServiceklageService opprettServiceklageService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final OidcUtils oidcUtils;

    @Transactional(dontRollbackOn = EksterntKallException.class)
    @PostMapping
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "opprettServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<OpprettServiceklageResponse>
            opprettServiceklage(@RequestBody OpprettServiceklageRequest request,
            @CookieValue(name = "selvbetjening-idtoken", required = false) String selvbetjening)
            throws DocumentException {

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
