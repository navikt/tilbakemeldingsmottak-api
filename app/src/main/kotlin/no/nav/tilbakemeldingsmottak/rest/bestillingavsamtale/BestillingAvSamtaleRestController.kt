package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilbakemeldingsmottak.api.BestillingAvSamtaleRestControllerApi
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.model.BestillSamtaleResponse
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service.BestillingAvSamtaleService
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.validation.BestillSamtaleValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = "azuread")
@RestController
class BestillingAvSamtaleRestController(private val bestillingAvSamtaleService: BestillingAvSamtaleService) :
    BestillingAvSamtaleRestControllerApi {
    private val bestillSamtaleValidator: BestillSamtaleValidator = BestillSamtaleValidator()

    @Transactional
    @Metrics(
        value = MetricLabels.DOK_REQUEST,
        extraTags = [MetricLabels.PROCESS_CODE, "bestill-samtale"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun bestillingAvSamtale(@RequestBody bestillSamtaleRequest: BestillSamtaleRequest): ResponseEntity<BestillSamtaleResponse> {
        bestillSamtaleValidator.validateRequest(bestillSamtaleRequest)
        bestillingAvSamtaleService.bestillSamtale(bestillSamtaleRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(BestillSamtaleResponse(message = "Samtale bestilt"))
    }
}
