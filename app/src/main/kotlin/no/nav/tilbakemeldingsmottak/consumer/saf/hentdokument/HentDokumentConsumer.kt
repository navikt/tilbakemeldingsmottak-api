package no.nav.tilbakemeldingsmottak.consumer.saf.hentdokument

import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.function.Consumer

@Component
class HentDokumentConsumer(
    @Value("\${hentdokument.url}") private val hentDokumentUrl: String,
    @Qualifier("safclient") private val webClient: WebClient
) : HentDokument {

    private val log = LoggerFactory.getLogger(javaClass)

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "hentDokument"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = true
    )
    @Retryable(include = [ServerErrorException::class], backoff = Backoff(delay = 3, multiplier = 500.0))
    override fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: String,
        token: String
    ): HentDokumentResponseTo {
        val httpHeaders = HttpHeadersUtil.createAuthHeaderFromToken(token)
        log.info(
            "Henter dokument fra saf journalpostId={}, dokumentInfoId={}, variantFormat={}",
            journalpostId,
            dokumentInfoId,
            variantFormat
        )
        val dokument = webClient
            .method(HttpMethod.GET)
            .uri("$hentDokumentUrl/$journalpostId/$dokumentInfoId/$variantFormat")
            .headers(getHttpHeadersAsConsumer(httpHeaders))
            .header("Nav-Callid", MDC.get(MDC_CALL_ID))
            .header("Nav-Consumer-Id", "srvtilbakemeldings")
            .retrieve()
            .bodyToMono(ByteArray::class.java)
            .doOnError { t: Throwable -> handleError(t, "saf (hent dokument)") }
            .block()
            ?: throw ServerErrorException(message = "SAF dokument responsen er null", errorCode = ErrorCode.SAF_ERROR)
        return mapResponse(dokument, journalpostId, dokumentInfoId, variantFormat)
    }

    private fun mapResponse(
        dokument: ByteArray,
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: String
    ): HentDokumentResponseTo {
        return try {
            HentDokumentResponseTo(dokument)
        } catch (e: Exception) {
            val errorMessage =
                "Kunne ikke dekode dokument, da dokumentet ikke er base64-encodet journalpostId=${journalpostId}, dokumentInfoId=${dokumentInfoId}, variantFormat=${variantFormat}. Feilmelding=${e.message}"
            throw ServerErrorException(errorMessage, e)
        }
    }

    private fun getHttpHeadersAsConsumer(httpHeaders: HttpHeaders): Consumer<HttpHeaders> {
        return Consumer { consumer: HttpHeaders -> consumer.addAll(httpHeaders) }
    }

    private fun handleError(error: Throwable, serviceName: String) {
        if (error is WebClientResponseException) {
            val statusCode: HttpStatusCode = error.statusCode
            val responseBody: String = error.responseBodyAsString
            val errorMessage =
                String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)

            if (statusCode === HttpStatus.UNAUTHORIZED) {
                throw ClientErrorUnauthorizedException(errorMessage, error, ErrorCode.SAF_UNAUTHORIZED)
            }
            if (statusCode === HttpStatus.FORBIDDEN) {
                throw ClientErrorForbiddenException(errorMessage, error, ErrorCode.SAF_FORBIDDEN)
            }
            if (statusCode === HttpStatus.NOT_FOUND) {
                throw ClientErrorNotFoundException(errorMessage, error, ErrorCode.SAF_NOT_FOUND)
            }
            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.SAF_ERROR)
            }
            throw ServerErrorException(errorMessage, error, ErrorCode.SAF_ERROR)
        }
    }

}
