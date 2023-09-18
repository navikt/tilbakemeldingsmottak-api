package no.nav.tilbakemeldingsmottak.rest.feilogmangler

import jakarta.transaction.Transactional
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilbakemeldingsmottak.api.FeilOgManglerRestControllerApi
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerResponse
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.service.FeilOgManglerService
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = "azuread")
@RestController
class FeilOgManglerRestController(
    private val feilOgManglerService: FeilOgManglerService,
    private val meldFeilOgManglerValidator: MeldFeilOgManglerValidator
) : FeilOgManglerRestControllerApi {
    @Transactional
    @Metrics(
        value = MetricLabels.DOK_REQUEST,
        extraTags = [MetricLabels.PROCESS_CODE, "meldFeilOgMangler"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun meldFeilOgMangler(@RequestBody meldFeilOgManglerRequest: MeldFeilOgManglerRequest): ResponseEntity<MeldFeilOgManglerResponse> {
        meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest)
        feilOgManglerService.meldFeilOgMangler(meldFeilOgManglerRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(MeldFeilOgManglerResponse(message = "Feil/mangel meldt"))
    }
}
