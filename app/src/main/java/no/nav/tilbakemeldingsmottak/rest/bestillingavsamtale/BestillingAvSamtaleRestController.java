package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Protected;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleResponse;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service.BestillingAvSamtaleService;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.validation.BestillSamtaleValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@Protected
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/rest/bestilling-av-samtale")
public class BestillingAvSamtaleRestController {

    private final BestillingAvSamtaleService bestillingAvSamtaleService;
    private final BestillSamtaleValidator bestillSamtaleValidator;


    @Inject
    public BestillingAvSamtaleRestController(final BestillingAvSamtaleService bestillingAvSamtaleService) {
        this.bestillingAvSamtaleService = bestillingAvSamtaleService;
        this.bestillSamtaleValidator = new BestillSamtaleValidator();
    }

    @Transactional
    @PostMapping
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "sendRos"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<BestillSamtaleResponse> sendRos(@RequestBody BestillSamtaleRequest request) throws SendEmailException {
            bestillSamtaleValidator.validateRequest(request);
            bestillingAvSamtaleService.bestillSamtale(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BestillSamtaleResponse.builder().message("Samtale bestilt").build());
    }
}
