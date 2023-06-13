package no.nav.tilbakemeldingsmottak.consumer.joark;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorForbiddenException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
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
public class JournalpostConsumer {

    private static final String FORSOEK_FERDIGSTILL = "?forsoekFerdigstill=true";
    @Inject
    @Qualifier("arkivClient")
    private WebClient webClient;
    @Value("${Journalpost_v1_url}")
    private String journalpostUrl;

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "opprettJournalpost"}, percentiles = {0.5, 0.95}, histogram = true)
    public OpprettJournalpostResponseTo opprettJournalpost(OpprettJournalpostRequestTo opprettJournalpostRequestTo) {
        log.info("Oppretter journalpost");

        OpprettJournalpostResponseTo journalpostReponse = webClient
                .method(HttpMethod.POST)
                .uri(journalpostUrl + "/journalpost/" + FORSOEK_FERDIGSTILL)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(opprettJournalpostRequestTo))
                .header("Nav-Callid", MDC.get(MDC_CALL_ID))
                .header("Nav-Consumer-Id", "srvtilbakemeldings")
                .retrieve()
                .bodyToMono(OpprettJournalpostResponseTo.class)
                .doOnError(t -> handleError(t, "JOARK (dokarkiv)"))
                .block();

        if (journalpostReponse == null) {
            throw new ServerErrorException("Klarte ikke å opprette journalpost", ErrorCode.DOKARKIV_ERROR);
        }

        log.info("Opprettet journalpost med journalpostId: {}", journalpostReponse.getJournalpostId());

        return journalpostReponse;

    }


    private void handleError(Throwable error, String serviceName) {
        if (error instanceof WebClientResponseException responseException) {
            var statusCode = responseException.getStatusCode();
            var responseBody = responseException.getResponseBodyAsString();
            var errorMessage = String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody);

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw new ClientErrorUnauthorizedException(errorMessage, responseException, ErrorCode.DOKARKIV_UNAUTHORIZED);
            }

            if (statusCode == HttpStatus.FORBIDDEN) {
                throw new ClientErrorForbiddenException(errorMessage, responseException, ErrorCode.DOKARKIV_FORBIDDEN);
            }

            if (statusCode.is4xxClientError()) {
                throw new ClientErrorException(errorMessage, responseException, ErrorCode.DOKARKIV_ERROR);
            }

            throw new ServerErrorException(errorMessage, responseException, ErrorCode.DOKARKIV_ERROR);

        }
    }

}