package no.nav.tilbakemeldingsmottak.consumer.ereg

import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class EregConsumer : Ereg {
    private val log: Logger = getLogger(javaClass)

    @Autowired
    @Qualifier("eregRestClient")
    lateinit var eregClient: RestClient

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "eregHentInfo"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun hentInfo(orgnr: String): String {
        val orgnrTrimmed = orgnr.trim()

        val eregResponse = eregClient
            .method(HttpMethod.GET)
            .uri("/v1/organisasjon/$orgnrTrimmed")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .header("Nav-Callid", MDC.get(MDC_CALL_ID))
            .header("Nav-Consumer-Id", "Tilbakemeldingsmottak")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                handleError(response, "Ereg")
            }
            .body(String::class.java)

        log.info("Hentet organisasjon $orgnrTrimmed : $eregResponse")

        return eregResponse ?: "Organisasjon $orgnrTrimmed ikke funnet"
    }

    private fun handleError(error: ClientHttpResponse, serviceName: String) {
        val statusCode = error.statusCode
        val responseBody = error.body
        val errorMessage = "Kall mot $serviceName feilet (statuskode: $statusCode). Body: $responseBody"

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            throw ClientErrorUnauthorizedException(errorMessage, null, ErrorCode.EREG_UNAUTHORIZED)
        }

        if (statusCode == HttpStatus.FORBIDDEN) {
            throw ClientErrorForbiddenException(errorMessage, null, ErrorCode.EREG_FORBIDDEN)
        }

        if (statusCode == HttpStatus.NOT_FOUND) {
            throw ClientErrorNotFoundException(errorMessage, null, ErrorCode.EREG_NOT_FOUND)
        }

        if (statusCode.is4xxClientError) {
            throw ClientErrorException(errorMessage, null, ErrorCode.EREG_ERROR)
        }

        throw ServerErrorException(errorMessage, null, ErrorCode.EREG_ERROR)
    }
}
