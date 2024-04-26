package no.nav.tilbakemeldingsmottak.consumer.oppgave

import no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo
import no.nav.tilbakemeldingsmottak.exceptions.*
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.BodyInserters
import java.lang.String.format

@Component
class OppgaveConsumer(
    @Qualifier("oppgaveRestClient") private val restClient: RestClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "opprettOppgave"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun opprettOppgave(opprettOppgaveRequestTo: OpprettOppgaveRequestTo): OpprettOppgaveResponseTo {
        log.info("Oppretter oppgave for journalpostId: {}", opprettOppgaveRequestTo.journalpostId)

        return restClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(opprettOppgaveRequestTo))
            .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                handleError(response, "oppgave (opprett oppgave)")
            }
            .body(OpprettOppgaveResponseTo::class.java)
            ?: throw ClientErrorException("Ingen response ved oppretting av oppgave for journalpostId = $opprettOppgaveRequestTo.journalpostId")
    }

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "endreOppgave"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun endreOppgave(endreOppgaveRequestTo: EndreOppgaveRequestTo): String {
        log.info(
            "Endrer oppgave for id: {} journalpostId: {}",
            endreOppgaveRequestTo.id,
            endreOppgaveRequestTo.journalpostId
        )

        return restClient.patch()
            .uri("/${endreOppgaveRequestTo.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(endreOppgaveRequestTo))
            .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                handleError(response, "oppgave (endre oppgave)")
            }
            .body(String::class.java)
            ?: "Ingen response ved endring av oppgave ${endreOppgaveRequestTo.id} for journalpostId ${endreOppgaveRequestTo.journalpostId}"
    }

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "hentOppgave"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun hentOppgave(oppgaveId: String): HentOppgaveResponseTo {
        log.info("Henter oppgave med id={}", oppgaveId)

        return restClient
            .method(HttpMethod.GET)
            .uri("/$oppgaveId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(oppgaveId))
            .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
            .retrieve()
            .body(HentOppgaveResponseTo::class.java)
            ?: throw ClientErrorException("Ingen oppgave hentet for oppgave id= $oppgaveId")
    }

    private fun handleError(response: ClientHttpResponse, serviceName: String) {
        val statusCode = response.statusCode
        val responseBody = response.body
        val errorMessage =
            format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            throw ClientErrorUnauthorizedException(errorMessage, null, ErrorCode.OPPGAVE_UNAUTHORIZED)
        }

        if (statusCode == HttpStatus.FORBIDDEN) {
            throw ClientErrorForbiddenException(errorMessage, null, ErrorCode.OPPGAVE_FORBIDDEN)
        }

        if (statusCode == HttpStatus.NOT_FOUND) {
            throw ClientErrorNotFoundException(errorMessage, null, ErrorCode.OPPGAVE_NOT_FOUND)
        }

        if (statusCode.is4xxClientError) {
            throw ClientErrorException(errorMessage, null, ErrorCode.OPPGAVE_ERROR)
        }

        throw ServerErrorException(errorMessage, null, ErrorCode.OPPGAVE_ERROR)
    }
}
