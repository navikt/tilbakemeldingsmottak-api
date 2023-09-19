package no.nav.tilbakemeldingsmottak.consumer.joark

import jakarta.inject.Inject
import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo
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
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class JournalpostConsumer {

    private val log: Logger = getLogger(javaClass)
    private val FORSOEK_FERDIGSTILL = "?forsoekFerdigstill=true"

    @Inject
    @Qualifier("arkivClient")
    private lateinit var webClient: WebClient

    @Value("\${Journalpost_v1_url}")
    private lateinit var journalpostUrl: String

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "opprettJournalpost"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun opprettJournalpost(opprettJournalpostRequestTo: OpprettJournalpostRequestTo): OpprettJournalpostResponseTo {
        log.info("Oppretter journalpost")

        val journalpostReponse = webClient
            .method(HttpMethod.POST)
            .uri("$journalpostUrl/journalpost/$FORSOEK_FERDIGSTILL")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(BodyInserters.fromValue(opprettJournalpostRequestTo))
            .header("Nav-Callid", MDC.get(MDC_CALL_ID))
            .header("Nav-Consumer-Id", "srvtilbakemeldings")
            .retrieve()
            .bodyToMono(OpprettJournalpostResponseTo::class.java)
            .doOnError { t -> handleError(t, "JOARK (dokarkiv)") }
            .block() ?: throw ServerErrorException("Klarte ikke å opprette journalpost", ErrorCode.DOKARKIV_ERROR)

        log.info("Opprettet journalpost med journalpostId: {}", journalpostReponse.journalpostId)

        return journalpostReponse
    }

    private fun handleError(error: Throwable, serviceName: String) {
        if (error is WebClientResponseException) {
            val statusCode = error.statusCode
            val responseBody = error.responseBodyAsString
            val errorMessage =
                String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw ClientErrorUnauthorizedException(errorMessage, error, ErrorCode.DOKARKIV_UNAUTHORIZED)
            }

            if (statusCode == HttpStatus.FORBIDDEN) {
                throw ClientErrorForbiddenException(errorMessage, error, ErrorCode.DOKARKIV_FORBIDDEN)
            }

            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.DOKARKIV_ERROR)
            }

            throw ServerErrorException(errorMessage, error, ErrorCode.DOKARKIV_ERROR)
        }
    }
}
