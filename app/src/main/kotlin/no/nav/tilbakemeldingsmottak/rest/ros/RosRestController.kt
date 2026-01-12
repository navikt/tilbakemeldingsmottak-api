package no.nav.tilbakemeldingsmottak.rest.ros

import no.nav.tilbakemeldingsmottak.api.RosRestControllerApi
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosResponse
import no.nav.tilbakemeldingsmottak.rest.ros.service.RosService
import no.nav.tilbakemeldingsmottak.rest.ros.validation.SendRosValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@issuerChecker.hasIssuer(authentication, {'azuread', 'tokenx'})")
class RosRestController(
    private val rosService: RosService,
    private val sendRosValidator: SendRosValidator,
) : RosRestControllerApi {

    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Transactional
    @Metrics(
        value = DOK_REQUEST,
        extraTags = [PROCESS_CODE, "sendRos"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = false
    )
    override fun sendRos(@RequestBody sendRosRequest: SendRosRequest): ResponseEntity<SendRosResponse> {
        logger.info("RosRequest: meldingslengde = ${sendRosRequest.melding?.length}")
        sendRosValidator.validateRequest(sendRosRequest)
        rosService.sendRos(sendRosRequest)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SendRosResponse("Ros sendt"))
    }
}
