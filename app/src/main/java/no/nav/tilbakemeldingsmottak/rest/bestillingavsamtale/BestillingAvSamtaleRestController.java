package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.tilbakemeldingsmottak.consumer.email.SendEmailException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleResponse;
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service.BestillingAvSamtaleService;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.validation.BestillSamtaleValidator;
import no.nav.tilbakemeldingsmottak.api.BestillingAvSamtaleRestControllerApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@ProtectedWithClaims(issuer = "azuread")
@RestController
@CrossOrigin(maxAge = 3600)
public class BestillingAvSamtaleRestController implements BestillingAvSamtaleRestControllerApi {

    private final BestillingAvSamtaleService bestillingAvSamtaleService;
    private final BestillSamtaleValidator bestillSamtaleValidator;


    @Inject
    public BestillingAvSamtaleRestController(final BestillingAvSamtaleService bestillingAvSamtaleService) {
        this.bestillingAvSamtaleService = bestillingAvSamtaleService;
        this.bestillSamtaleValidator = new BestillSamtaleValidator();
    }

    @Transactional
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "sendRos"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<BestillSamtaleResponse> bestillingAvSamtale(@RequestBody BestillSamtaleRequest request) throws SendEmailException {
            bestillSamtaleValidator.validateRequest(request);
            bestillingAvSamtaleService.bestillSamtale(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BestillSamtaleResponse.builder().message("Samtale bestilt").build());
    }
}
