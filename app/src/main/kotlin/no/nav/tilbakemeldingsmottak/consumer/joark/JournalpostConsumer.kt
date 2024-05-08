package no.nav.tilbakemeldingsmottak.consumer.joark

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
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.BodyInserters

@Component
class JournalpostConsumer(
    @Qualifier("arkivRestClient") private val restClient: RestClient,
) {

    private val log: Logger = getLogger(javaClass)
    private val FORSOEK_FERDIGSTILL = "?forsoekFerdigstill=true"

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "opprettJournalpost"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun opprettJournalpost(opprettJournalpostRequestTo: OpprettJournalpostRequestTo): OpprettJournalpostResponseTo {
        val callId = MDC.get(MDC_CALL_ID)
        log.info("Oppretter journalpost for callId {}", callId)

        val journalpostReponse = restClient
            .method(HttpMethod.POST)
            .uri("/journalpost/$FORSOEK_FERDIGSTILL")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(opprettJournalpostRequestTo)
            .header("Nav-Callid", callId)
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                handleError(response, "JOARK (dokarkiv)")
            }
            .body(OpprettJournalpostResponseTo::class.java)
            ?: throw ClientErrorException("Kall mot JOARK (dokarkiv) feilet")

        log.info("Opprettet journalpost med journalpostId: {}", journalpostReponse.journalpostId)

        return journalpostReponse
    }

    private fun handleError(response: ClientHttpResponse, serviceName: String) {
        val statusCode = response.statusCode
        val responseBody = response.body
        val errorMessage =
            String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            throw ClientErrorUnauthorizedException(errorMessage, null, ErrorCode.DOKARKIV_UNAUTHORIZED)
        }

        if (statusCode == HttpStatus.FORBIDDEN) {
            throw ClientErrorForbiddenException(errorMessage, null, ErrorCode.DOKARKIV_FORBIDDEN)
        }

        if (statusCode.is4xxClientError) {
            throw ClientErrorException(errorMessage, null, ErrorCode.DOKARKIV_ERROR)
        }

        throw ServerErrorException(errorMessage, null, ErrorCode.DOKARKIV_ERROR)
    }
}
