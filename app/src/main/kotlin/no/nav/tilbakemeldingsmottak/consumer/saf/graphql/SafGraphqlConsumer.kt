package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.saf.generated.HENT_JOURNALPOST
import no.nav.tilbakemeldingsmottak.saf.generated.HentJournalpost
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.*
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.function.Consumer

@Component
class SafGraphqlConsumer(
    @Value("\${saf.graphql.url}") private val graphQLurl: String,
    @Qualifier("safQlClient") private val webClient: GraphQlClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "safJournalpostquery"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    @Retryable(include = [ServerErrorException::class], maxAttempts = 3, backoff = Backoff(delay = 500))
    fun performQuery(graphQLRequest: GraphQLRequest): Journalpost {

        /*
                val query =     "query ( ${'$'}queryJournalpostId: String!) {\n    journalpost(journalpostId: ${'$'}queryJournalpostId) {\n    bruker {\n      id\n    }\n    datoOpprettet\n    dokumenter {\n      dokumentInfoId\n      dokumentvarianter {\n        saksbehandlerHarTilgang\n        variantformat\n      }\n    }\n  }\n}"
        */

        val response = webClient.document(HENT_JOURNALPOST)
            .variables(graphQLRequest.variables)
            .retrieve("journalpost")
            .toEntity(Journalpost::class.java)
            .doOnError(Consumer { error: Throwable -> handleError(error, "SAF journalpost") })
            .block()

        if (response == null) {
            val raw = webClient.document(HENT_JOURNALPOST)
                .variables(graphQLRequest.variables)
                .retrieve("journalpost")
                .toEntity(Journalpost::class.java)
                .block()

            logger.info("Raw GraphQL response: $raw")

            throw ClientErrorNotFoundException(
                message = "Ingen journalpost ble funnet",
                errorCode = ErrorCode.SAF_NOT_FOUND
            )
        }

        return response
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
