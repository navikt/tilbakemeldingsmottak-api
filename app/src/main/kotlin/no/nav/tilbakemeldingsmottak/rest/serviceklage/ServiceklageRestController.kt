package no.nav.tilbakemeldingsmottak.rest.serviceklage

import no.nav.security.token.support.core.api.Protected
import no.nav.tilbakemeldingsmottak.api.ServiceklageRestControllerApi
import no.nav.tilbakemeldingsmottak.config.Constants.TOKENX_ISSUER
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.metrics.MetricsUtils
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageResponse
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator
import no.nav.tilbakemeldingsmottak.util.OidcUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Protected
@RestController
class ServiceklageRestController(
    private val opprettServiceklageService: OpprettServiceklageService,
    private val opprettServiceklageValidator: OpprettServiceklageValidator,
    private val oidcUtils: OidcUtils
) : ServiceklageRestControllerApi {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(noRollbackFor = [EksterntKallException::class])
    @Metrics(
        value = DOK_REQUEST,
        extraTags = [PROCESS_CODE, "opprettServiceklage"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = false
    )
    override fun opprettServiceklage(@RequestBody opprettServiceklageRequest: OpprettServiceklageRequest): ResponseEntity<OpprettServiceklageResponse> {
        log.info("Mottatt serviceklage via skjema på nav.no")
        val paloggetBruker = oidcUtils.getPidForIssuer(TOKENX_ISSUER)
        val innlogget = paloggetBruker != null
        log.info("Bruker er innlogget $innlogget")

        opprettServiceklageValidator.validateRequest(opprettServiceklageRequest, paloggetBruker)

        val opprettServiceklageResponse =
            opprettServiceklageService.opprettServiceklage(opprettServiceklageRequest, innlogget)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(opprettServiceklageResponse)
    }

}
