package no.nav.tilbakemeldingsmottak.consumer.oppgave;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.*;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class OppgaveConsumer {

    private final String oppgaveUrl;
    @Inject
    @Qualifier("oppgaveClient")
    private WebClient webClient;

    public OppgaveConsumer(@Value("${oppgave_oppgaver_url}") String oppgaveUrl) {
        this.oppgaveUrl = oppgaveUrl;
    }


    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "opprettOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public OpprettOppgaveResponseTo opprettOppgave(OpprettOppgaveRequestTo opprettOppgaveRequestTo) {
        log.info("Oppretter oppgave for journalpostId: {}", opprettOppgaveRequestTo.getJournalpostId());

        var oppgaveResponse = webClient
                .method(HttpMethod.POST)
                .uri(oppgaveUrl)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(opprettOppgaveRequestTo))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .bodyToMono(OpprettOppgaveResponseTo.class)
                .doOnError(t -> handleError(t, "oppgave (opprett oppgave)"))
                .block();

        if (oppgaveResponse == null) {
            throw new ServerErrorException("Klarte ikke opprette oppgave", ErrorCode.OPPGAVE_ERROR);
        }

        return oppgaveResponse;
    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "endreOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public String endreOppgave(EndreOppgaveRequestTo endreOppgaveRequestTo) {
        log.info("Endrer oppgave for id: {} journalpostId: {}", endreOppgaveRequestTo.getId(), endreOppgaveRequestTo.getJournalpostId());

        var oppgaveResponse = webClient
                .method(HttpMethod.PATCH)
                .uri(oppgaveUrl + "/" + endreOppgaveRequestTo.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(endreOppgaveRequestTo))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(t -> handleError(t, "oppgave (endre oppgave)"))
                .block();

        if (oppgaveResponse == null) {
            throw new ServerErrorException("Klarte ikke endre oppgave", ErrorCode.OPPGAVE_ERROR);
        }

        return oppgaveResponse;

    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public HentOppgaveResponseTo hentOppgave(String oppgaveId) {
        log.info("Henter oppgave med id={}", oppgaveId);

        var oppgaveResponse = webClient
                .method(HttpMethod.GET)
                .uri(oppgaveUrl + "/" + oppgaveId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(oppgaveId))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .bodyToMono(HentOppgaveResponseTo.class)
                .doOnError(t -> handleError(t, "oppgave (hent oppgave)"))
                .block();

        if (oppgaveResponse == null) {
            throw new ServerErrorException("Klarte ikke hente oppgave", ErrorCode.OPPGAVE_ERROR);
        }

        return oppgaveResponse;

    }


    private void handleError(Throwable error, String serviceName) {
        if (error instanceof WebClientResponseException responseException) {
            var statusCode = responseException.getStatusCode();
            var responseBody = responseException.getResponseBodyAsString();
            var errorMessage = String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody);

            if (statusCode.is4xxClientError()) {
                if (statusCode == HttpStatus.FORBIDDEN || statusCode == HttpStatus.UNAUTHORIZED) {
                    throw new ClientErrorUnauthorizedException(errorMessage, responseException, ErrorCode.OPPGAVE_UNAUTHORIZED);
                } else if (statusCode == HttpStatus.NOT_FOUND) {
                    throw new ClientErrorNotFoundException(errorMessage, responseException, ErrorCode.OPPGAVE_NOT_FOUND);
                } else {
                    throw new ClientErrorException(errorMessage, responseException, ErrorCode.OPPGAVE_ERROR);
                }
            } else {
                throw new ServerErrorException(errorMessage, responseException, ErrorCode.OPPGAVE_ERROR);
            }
        }
    }
}