package no.nav.tilbakemeldingsmottak.consumer.oppgave;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.EndreOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.OpprettOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.EndreOppgaveFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.EndreOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.HentOppgaveFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.HentOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.inject.Inject;

@Slf4j
@Component
public class OppgaveConsumer {

    @Inject
    @Qualifier("serviceuserclient")
    private WebClient webClient;

    private final String oppgaveUrl;

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
                .onStatus(HttpStatus::isError, statusResponse -> {
                    log.error(String.format("OpprettOppgave feilet med statusKode=%s", statusResponse.statusCode()));
                    if (statusResponse.statusCode().is5xxServerError()) {
                        throw new OpprettOppgaveTechnicalException(String.format("OpprettOppgave feilet teknisk med statusKode=%s.", statusResponse
                                .statusCode()), new RuntimeException("Kall mot arkivet feilet"));
                    } else if (statusResponse.statusCode().is4xxClientError()) {
                        throw new OpprettOppgaveFunctionalException(String.format("OpprettOppgave feilet funksjonelt med statusKode=%s.", statusResponse
                                .statusCode()), new RuntimeException("Kall mot arkivet feilet"));
                    }
                    return Mono.error(new IllegalStateException(
                            String.format("OpprettOppgave feilet med statusKode=%s", statusResponse.statusCode())));

                })
                .bodyToMono(OpprettOppgaveResponseTo.class)
                .block();
    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "endreOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public String endreOppgave(EndreOppgaveRequestTo endreOppgaveRequestTo) {
        log.debug("Endrer oppgave");

        return webClient
                .method(HttpMethod.PATCH)
                .uri(oppgaveUrl +"/" + endreOppgaveRequestTo.getId())
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(endreOppgaveRequestTo))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .onStatus(HttpStatus::isError, statusResponse -> {
                    log.error(String.format("EndreOppgave feilet med statusKode=%s", statusResponse.statusCode()));
                    if (statusResponse.statusCode().is5xxServerError()) {
                        throw new EndreOppgaveTechnicalException(String.format("EndreOppgave feilet teknisk med statusKode=%s.", statusResponse
                                .statusCode()), new RuntimeException("Kall mot arkivet feilet"));
                    } else if (statusResponse.statusCode().is4xxClientError()) {
                        throw new EndreOppgaveFunctionalException(String.format("EndreOppgave feilet funksjonelt med statusKode=%s.", statusResponse
                                .statusCode()), new RuntimeException("Kall mot arkivet feilet"));
                    }
                    return Mono.error(new IllegalStateException(
                            String.format("EndreOppgave feilet med statusKode=%s", statusResponse.statusCode())));

                })
                .bodyToMono(String.class)
                .block();

    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentOppgave"}, percentiles = {0.5, 0.95}, histogram = true)
    public HentOppgaveResponseTo hentOppgave(String oppgaveId) {
            log.debug("Henter oppgave med id={}", oppgaveId);

        return webClient
                .method(HttpMethod.GET)
                .uri(oppgaveUrl+"/"+oppgaveId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(oppgaveId))
                .header("X-Correlation-ID", MDC.get(MDC_CALL_ID))
                .retrieve()
                .onStatus(HttpStatus::isError, statusResponse -> {
                    log.error(String.format("HentOppgave feilet med statusKode=%s", statusResponse.statusCode()));
                    if (statusResponse.statusCode().is5xxServerError()) {
                        throw new HentOppgaveTechnicalException(String.format("HentOppgave feilet teknisk med statusKode=%s.", statusResponse
                                .statusCode()), new RuntimeException("Kall mot arkivet feilet"));
                    } else if (statusResponse.statusCode().is4xxClientError()) {
                        throw new HentOppgaveFunctionalException(String.format("HentOppgave feilet funksjonelt med statusKode=%s.", statusResponse
                                .statusCode()), new RuntimeException("Kall mot arkivet feilet"));
                    }
                    return Mono.error(new IllegalStateException(
                            String.format("HentOppgave feilet med statusKode=%s", statusResponse.statusCode())));

                })
                .bodyToMono(HentOppgaveResponseTo.class)
                .block();

    }
}
