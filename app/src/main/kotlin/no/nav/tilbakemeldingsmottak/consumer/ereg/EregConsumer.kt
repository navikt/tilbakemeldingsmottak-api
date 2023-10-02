package no.nav.tilbakemeldingsmottak.consumer.ereg

import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class EregConsumer(
    @Value("\${ereg.api.url}") private val eregApiUrl: String,
    @Qualifier("eregClient") private val webClient: WebClient
) : Ereg {
    private val log: Logger = getLogger(javaClass)

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "eregHentInfo"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun hentInfo(orgnr: String): String {
        val orgnrTrimmed = orgnr.trim()

        val eregResponse = webClient
            .method(HttpMethod.GET)
            .uri("$eregApiUrl/v1/organisasjon/$orgnrTrimmed")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .header("Nav-Callid", MDC.get(MDC_CALL_ID))
            .header("Nav-Consumer-Id", "Tilbakemeldingsmottak")
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnError { t -> handleError(t, "Ereg") }
            .block() ?: throw ServerErrorException(
            message = "Klarte ikke å hente info om organisasjon",
            errorCode = ErrorCode.EREG_ERROR
        )

        log.info("Hentet organisasjon $orgnrTrimmed : $eregResponse")

        return eregResponse
    }

    private fun handleError(error: Throwable, serviceName: String) {
        if (error is WebClientResponseException) {
            val statusCode = error.statusCode
            val responseBody = error.responseBodyAsString
            val errorMessage = "Kall mot $serviceName feilet (statuskode: $statusCode). Body: $responseBody"

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw ClientErrorUnauthorizedException(errorMessage, error, ErrorCode.EREG_UNAUTHORIZED)
            }

            if (statusCode == HttpStatus.FORBIDDEN) {
                throw ClientErrorForbiddenException(errorMessage, error, ErrorCode.EREG_FORBIDDEN)
            }

            if (statusCode == HttpStatus.NOT_FOUND) {
                throw ClientErrorNotFoundException(errorMessage, error, ErrorCode.EREG_NOT_FOUND)
            }

            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.EREG_ERROR)
            }

            throw ServerErrorException(errorMessage, error, ErrorCode.EREG_ERROR)
        }
    }
}
