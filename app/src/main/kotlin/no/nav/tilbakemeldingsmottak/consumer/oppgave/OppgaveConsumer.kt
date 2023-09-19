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
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.lang.String.format

@Component
class OppgaveConsumer(
    @Value("\${oppgave_oppgaver_url}") private val oppgaveUrl: String,
    @Qualifier("oppgaveClient") private val webClient: WebClient
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
        log.info("Oppretter oppgave med {}", opprettOppgaveRequestTo)

        return webClient
            .method(HttpMethod.POST)
            .uri(oppgaveUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(opprettOppgaveRequestTo))
            .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
            .retrieve()
            .bodyToMono(OpprettOppgaveResponseTo::class.java)
            .doOnError { t -> handleError(t, "oppgave (opprett oppgave)") }
            .block() ?: throw ServerErrorException("Klarte ikke opprette oppgave", ErrorCode.OPPGAVE_ERROR)
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

        return webClient
            .method(HttpMethod.PATCH)
            .uri("$oppgaveUrl/${endreOppgaveRequestTo.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(endreOppgaveRequestTo))
            .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnError { t -> handleError(t, "oppgave (endre oppgave)") }
            .block() ?: throw ServerErrorException("Klarte ikke endre oppgave", ErrorCode.OPPGAVE_ERROR)
    }

    @Metrics(
        value = DOK_CONSUMER,
        extraTags = [PROCESS_CODE, "hentOppgave"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    fun hentOppgave(oppgaveId: String): HentOppgaveResponseTo {
        log.info("Henter oppgave med id={}", oppgaveId)

        return webClient
            .method(HttpMethod.GET)
            .uri("$oppgaveUrl/$oppgaveId")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(oppgaveId))
            .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
            .retrieve()
            .bodyToMono(HentOppgaveResponseTo::class.java)
            .doOnError { t -> handleError(t, "oppgave (hent oppgave)") }
            .block() ?: throw ServerErrorException("Klarte ikke hente oppgave", ErrorCode.OPPGAVE_ERROR)
    }

    private fun handleError(error: Throwable, serviceName: String) {
        if (error is WebClientResponseException) {
            val statusCode = error.statusCode
            val responseBody = error.responseBodyAsString
            val errorMessage =
                format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody)

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw ClientErrorUnauthorizedException(errorMessage, error, ErrorCode.OPPGAVE_UNAUTHORIZED)
            }

            if (statusCode == HttpStatus.FORBIDDEN) {
                throw ClientErrorForbiddenException(errorMessage, error, ErrorCode.OPPGAVE_FORBIDDEN)
            }

            if (statusCode == HttpStatus.NOT_FOUND) {
                throw ClientErrorNotFoundException(errorMessage, error, ErrorCode.OPPGAVE_NOT_FOUND)
            }

            if (statusCode.is4xxClientError) {
                throw ClientErrorException(errorMessage, error, ErrorCode.OPPGAVE_ERROR)
            }

            throw ServerErrorException(errorMessage, error, ErrorCode.OPPGAVE_ERROR)
        }
    }
}
