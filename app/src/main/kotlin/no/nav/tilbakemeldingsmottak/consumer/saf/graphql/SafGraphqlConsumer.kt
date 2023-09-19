package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import jakarta.inject.Inject
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost
import no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.function.Consumer

@Component
class SafGraphqlConsumer @Inject constructor(
    @Value("\${saf.graphql.url}") private val graphQLurl: String,
    @Qualifier("safclient") private val webClient: WebClient
) {

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "safJournalpostquery"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    @Retryable(include = [ServerErrorException::class], maxAttempts = 3, backoff = Backoff(delay = 500))
    fun performQuery(graphQLRequest: GraphQLRequest, authorizationHeader: String?): SafJournalpostTo {
        val httpHeaders = HttpHeadersUtil.createAuthHeaderFromToken(authorizationHeader)
        val response = webClient
            .method(HttpMethod.POST)
            .uri(graphQLurl)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(graphQLRequest))
            .headers(getHttpHeadersAsConsumer(httpHeaders))
            .retrieve()
            .bodyToMono(SafJsonJournalpost::class.java)
            .doOnError { t: Throwable -> handleError(t, "saf graphql (hent journalpost info)") }
            .block()

        if (response?.data == null || response.journalpost == null) {
            throw ClientErrorNotFoundException(
                message = "Ingen journalpost ble funnet",
                errorCode = ErrorCode.SAF_NOT_FOUND
            )
        }

        return response.journalpost!!
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
            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.SAF_ERROR)
            }
            throw ServerErrorException(errorMessage, error, ErrorCode.SAF_ERROR)
        }
    }
}
