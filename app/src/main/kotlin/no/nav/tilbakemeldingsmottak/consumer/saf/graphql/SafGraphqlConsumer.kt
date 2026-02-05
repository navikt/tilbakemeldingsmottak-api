package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import io.github.resilience4j.retry.annotation.Retry
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.saf.generated.HENT_JOURNALPOST
import no.nav.tilbakemeldingsmottak.saf.generated.hentjournalpost.Journalpost
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.graphql.client.GraphQlClient
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.util.function.Consumer

@Component
class SafGraphqlConsumer(
    @Qualifier("safQlClient") private val webClient: GraphQlClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "safJournalpostquery"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    @Retry(name = "safGraphQl")
    fun performQuery(graphQLRequest: GraphQLRequest): Journalpost {

        logger.info("GraphQL hent journalpost")
        val response = webClient.document(HENT_JOURNALPOST)
            .variables(graphQLRequest.variables)
            .retrieve(graphQLRequest.operationName)
            .toEntity(Journalpost::class.java)
            .doOnError(Consumer { error: Throwable -> handleError(error, "SAF journalpost") })
            .block()
        logger.info("GraphQL hentet journalpost")

        if (response == null) {
            val raw = webClient.document(HENT_JOURNALPOST)
                .variables(graphQLRequest.variables)
                .retrieve("journalpost")
                .toEntity(Journalpost::class.java)
                .block()

            logger.info("Funnet raw GraphQL response ${raw != null}")

            throw ClientErrorNotFoundException(
                message = "Ingen journalpost ble funnet",
                errorCode = ErrorCode.SAF_NOT_FOUND
            )
        }

        return response
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
