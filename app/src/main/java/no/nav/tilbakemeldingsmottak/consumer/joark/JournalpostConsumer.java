package no.nav.tilbakemeldingsmottak.consumer.joark;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
public class JournalpostConsumer {

    private static final String FORSOEK_FERDIGSTILL = "?forsoekFerdigstill=true";
    @Inject
    @Qualifier("arkivClient")
    private WebClient webClient;
    @Value("${Journalpost_v1_url}")
    private String journalpostUrl;

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "opprettJournalpost"}, percentiles = {0.5, 0.95}, histogram = true)
    public OpprettJournalpostResponseTo opprettJournalpost(OpprettJournalpostRequestTo opprettJournalpostRequestTo) {
        if (log.isDebugEnabled()) {
            log.debug("Oppretter journalpost");
        }
        OpprettJournalpostResponseTo journalpostReponse = webClient
                .method(HttpMethod.POST)
                .uri(journalpostUrl + "/journalpost/" + FORSOEK_FERDIGSTILL)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(opprettJournalpostRequestTo))
                .header("Nav-Callid", MDC.get(MDC_CALL_ID))
                .header("Nav-Consumer-Id", "srvtilbakemeldings")
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::errorResponse)
                .bodyToMono(OpprettJournalpostResponseTo.class)
                .block();

        if (log.isDebugEnabled()) {
            log.debug("Journalpost med journalpostId={} opprettet", journalpostReponse.getJournalpostId());
        }

        return journalpostReponse;

    }

    private Mono<Throwable> errorResponse(ClientResponse statusResponse) {

        if (statusResponse.statusCode().is4xxClientError()) {
            // ClientErrorUnauthorizedException og ClientErrorException er vanligvis ikke logget som error, men nødvendig her
            log.error("OpprettJournalpost feilet med statusKode={}", statusResponse.statusCode());

            if (statusResponse.statusCode().value() == HttpStatus.FORBIDDEN.value() || statusResponse.statusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                return statusResponse.bodyToMono(String.class).flatMap(body ->
                        Mono.error(new ClientErrorUnauthorizedException(String.format("Autentisering mot JOARK (dokarkiv) feilet (statusCode: %s)", statusResponse.statusCode()), new RuntimeException(body), ErrorCode.DOKARKIV_UNAUTHORIZED))
                );
            }

            return statusResponse.bodyToMono(String.class).flatMap(body ->
                    Mono.error(new ClientErrorException(String.format("Kall mot JOARK (dokarkiv) feilet (statusCode: %s)", statusResponse.statusCode()), new RuntimeException(body), ErrorCode.DOKARKIV_ERROR))
            );
        }

        return statusResponse.bodyToMono(String.class).flatMap(body ->
                Mono.error(new ServerErrorException(String.format("Kall mot JOARK (dokarkiv) feilet (statusCode: %s)", statusResponse.statusCode()), new RuntimeException(body), ErrorCode.DOKARKIV_ERROR))
        );
    }

}