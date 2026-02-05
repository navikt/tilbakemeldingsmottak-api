package no.nav.tilbakemeldingsmottak.consumer.norg2

import io.github.resilience4j.retry.annotation.Retry
import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.config.cache.CacheConfig
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class Norg2Consumer(
    @Qualifier("norg2RestClient") private val restClient: RestClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${norg2.api.v1.url}")
    private val norg2Url: String? = null

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "hentEnheter"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    @Retry(name = "norg2")
    @Cacheable(
        CacheConfig.NORG2_CACHE
    )
    fun hentEnheter(): List<Enhet> {
        log.info("Henter enheter fra norg2 på $norg2Url")

        val response = restClient
            .method(HttpMethod.GET)
            .uri("/enhet")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Nav-Callid", MDC.get(MDC_CALL_ID))
            .header("Nav-Consumer-Id", "Tilbakemeldingsmottak")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                handleError(response, "norg2")
            }
            .body(object : ParameterizedTypeReference<List<Enhet>>() {})
            ?: throw ClientErrorException("Kall mot norg2 feilet")

        log.info("Hentet enheter ${response.size} enheter fra norg2")

        return response
    }

    private fun handleError(response: ClientHttpResponse, serviceName: String) {
        log.info("Inside handleError")
        log.info("Inside WebClientResponseException")

        val statusCode: HttpStatusCode = response.statusCode
        val responseBody = response.body
        val errorMessage =
            String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)
        if (statusCode === HttpStatus.UNAUTHORIZED) {
            throw ClientErrorUnauthorizedException(errorMessage, null, ErrorCode.NORG2_UNAUTHORIZED)
        }
        if (statusCode === HttpStatus.FORBIDDEN) {
            throw ClientErrorForbiddenException(errorMessage, null, ErrorCode.NORG2_FORBIDDEN)
        }
        if (statusCode.is4xxClientError) {
            throw ClientErrorException(errorMessage, null, ErrorCode.NORG2_ERROR)
        }
        throw ServerErrorException(errorMessage, null, ErrorCode.NORG2_ERROR)
    }

}
