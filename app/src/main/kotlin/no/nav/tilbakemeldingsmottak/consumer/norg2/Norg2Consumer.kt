package no.nav.tilbakemeldingsmottak.consumer.norg2

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
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class Norg2Consumer(
    @Value("\${norg2.api.v1.url}") private val norg2Url: String,
    @Qualifier("norg2Client") private val webClient: WebClient
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "hentEnheter"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = true
    )
    @Retryable(include = [ServerErrorException::class], backoff = Backoff(delay = 1000))
    @Cacheable(
        CacheConfig.NORG2_CACHE
    )
    fun hentEnheter(): List<Enhet> {
        log.info("Henter enheter fra norg2")

        val response = webClient
            .method(HttpMethod.GET)
            .uri("$norg2Url/enhet")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Nav-Callid", MDC.get(MDC_CALL_ID))
            .header("Nav-Consumer-Id", "Tilbakemeldingsmottak")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Enhet>>() {})
            .doOnError { error -> handleError(error, "norg2") }
            .block() ?: emptyList()

        log.info("Hentet enheter ${response.size} enheter fra norg2")

        return response
    }

    private fun handleError(error: Throwable, serviceName: String) {
        log.info("Inside handleError")
        if (error is WebClientResponseException) {
            log.info("Inside WebClientResponseException")

            val statusCode: HttpStatusCode = error.statusCode
            val responseBody: String = error.responseBodyAsString
            val errorMessage =
                String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)
            if (statusCode === HttpStatus.UNAUTHORIZED) {
                throw ClientErrorUnauthorizedException(errorMessage, error, ErrorCode.NORG2_UNAUTHORIZED)
            }
            if (statusCode === HttpStatus.FORBIDDEN) {
                throw ClientErrorForbiddenException(errorMessage, error, ErrorCode.NORG2_FORBIDDEN)
            }
            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.NORG2_ERROR)
            }
            throw ServerErrorException(errorMessage, error, ErrorCode.NORG2_ERROR)
        }
    }

}
