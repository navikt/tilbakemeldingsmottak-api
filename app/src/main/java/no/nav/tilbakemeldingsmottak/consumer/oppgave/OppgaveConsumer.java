package no.nav.tilbakemeldingsmottak.consumer.oppgave;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        log.debug("Oppretter oppgave");

        return webClient
                .method(HttpMethod.POST)
                .uri(oppgaveUrl)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(opprettOppgaveRequestTo))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .onStatus(HttpStatusCode::isError, statusResponse -> errorResponse(statusResponse, "oppgave (opprett oppgave)"))
                .bodyToMono(OpprettOppgaveResponseTo.class)
                .block();
    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "endreOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public String endreOppgave(EndreOppgaveRequestTo endreOppgaveRequestTo) {
        log.debug("Endrer oppgave");

        return webClient
                .method(HttpMethod.PATCH)
                .uri(oppgaveUrl + "/" + endreOppgaveRequestTo.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(endreOppgaveRequestTo))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .onStatus(HttpStatusCode::isError, statusResponse -> errorResponse(statusResponse, "oppgave (endre oppgave)"))
                .bodyToMono(String.class)
                .block();

    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public HentOppgaveResponseTo hentOppgave(String oppgaveId) {
        log.debug("Henter oppgave med id={}", oppgaveId);

        return webClient
                .method(HttpMethod.GET)
                .uri(oppgaveUrl + "/" + oppgaveId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(oppgaveId))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .onStatus(HttpStatusCode::isError, statusResponse -> errorResponse(statusResponse, "oppgave (hent oppgave)"))
                .bodyToMono(HentOppgaveResponseTo.class)
                .block();

    }

    private Mono<Throwable> errorResponse(ClientResponse statusResponse, String tjeneste) {
        if (statusResponse.statusCode().is4xxClientError()) {
            if (statusResponse.statusCode().value() == 403 || statusResponse.statusCode().value() == 401) {
                return statusResponse.bodyToMono(String.class).flatMap(body ->
                        Mono.error(new ClientErrorUnauthorizedException(String.format("Autentisering mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.OPPGAVE_UNAUTHORIZED))
                );
            }

            log.error("Kall mot {} feilet med statuskode {}", tjeneste, statusResponse.statusCode());

            return statusResponse.bodyToMono(String.class).flatMap(body ->
                    Mono.error(new ClientErrorException(String.format("Kall mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.OPPGAVE_ERROR))
            );
        }

        return statusResponse.bodyToMono(String.class).flatMap(body ->
                Mono.error(new ServerErrorException(String.format("Kall mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.OPPGAVE_ERROR))
        );
    }
}
