package no.nav.tilbakemeldingsmottak.rest.feilogmangler

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilbakemeldingsmottak.api.FeilOgManglerRestControllerApi
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.metrics.MetricsUtils
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerResponse
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.service.FeilOgManglerService
import no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation.MeldFeilOgManglerValidator
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = "azuread")
@RestController
class FeilOgManglerRestController(
    private val feilOgManglerService: FeilOgManglerService,
    private val meldFeilOgManglerValidator: MeldFeilOgManglerValidator,
    private val metricsUtils: MetricsUtils,
    private val oidcUtils: OidcUtils
) : FeilOgManglerRestControllerApi {
    @Transactional
    @Metrics(
        value = MetricLabels.DOK_REQUEST,
        extraTags = [MetricLabels.PROCESS_CODE, "meldFeilOgMangler"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun meldFeilOgMangler(@RequestBody meldFeilOgManglerRequest: MeldFeilOgManglerRequest): ResponseEntity<MeldFeilOgManglerResponse> {
        if (oidcUtils.getPidForIssuer(Constants.TOKENX_ISSUER) == null) {
            metricsUtils.incrementNotLoggedInRequestCounter(this.javaClass.name, "meldFeilOgMangler")
        }
        meldFeilOgManglerValidator.validateRequest(meldFeilOgManglerRequest)
        feilOgManglerService.meldFeilOgMangler(meldFeilOgManglerRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(MeldFeilOgManglerResponse(message = "Feil/mangel meldt"))
    }
}
