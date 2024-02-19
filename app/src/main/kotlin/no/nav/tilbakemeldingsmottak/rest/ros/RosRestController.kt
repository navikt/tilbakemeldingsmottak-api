package no.nav.tilbakemeldingsmottak.rest.ros

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilbakemeldingsmottak.api.RosRestControllerApi
import no.nav.tilbakemeldingsmottak.config.Constants
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.metrics.MetricsUtils
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosResponse
import no.nav.tilbakemeldingsmottak.rest.ros.service.RosService
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = "azuread")
@RestController
class RosRestController(
    private val rosService: RosService,
    private val sendRosValidator: SendRosValidator,
    private val metricsUtils: MetricsUtils,
    private val oidcUtils: OidcUtils
) : RosRestControllerApi {

    @Transactional
    @Metrics(
        value = DOK_REQUEST,
        extraTags = [PROCESS_CODE, "sendRos"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = false
    )
    override fun sendRos(@RequestBody sendRosRequest: SendRosRequest): ResponseEntity<SendRosResponse> {
        /*
                if (oidcUtils.getPidForIssuer(Constants.TOKENX_ISSUER) == null) {
                    metricsUtils.incrementNotLoggedInRequestCounter(this.javaClass.name, "sendRos")
                }
        */
        sendRosValidator.validateRequest(sendRosRequest)
        rosService.sendRos(sendRosRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SendRosResponse("Ros sendt"))
    }
}
