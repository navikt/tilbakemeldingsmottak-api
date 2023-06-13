package no.nav.tilbakemeldingsmottak.consumer.saf.graphql;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorForbiddenException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorNotFoundException;
import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorUnauthorizedException;
import no.nav.tilbakemeldingsmottak.exceptions.ErrorCode;
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Consumer;

import static no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil.createAuthHeaderFromToken;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Slf4j
public class SafGraphqlConsumer {

    private final String graphQLurl;
    @Inject
    @Qualifier("safclient")
    private WebClient webClient;

    @Inject
    public SafGraphqlConsumer(@Value("${saf.graphql.url}") String graphQLurl) {
        this.graphQLurl = graphQLurl;
    }

    @Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "safJournalpostquery"}, percentiles = {0.5, 0.95}, histogram = true)
    @Retryable(include = ServerErrorException.class, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public SafJournalpostTo performQuery(GraphQLRequest graphQLRequest, String authorizationHeader) {

        HttpHeaders httpHeaders = createAuthHeaderFromToken(authorizationHeader);
        SafJsonJournalpost respons = webClient
                .method(HttpMethod.POST)
                .uri(graphQLurl)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(BodyInserters.fromValue(graphQLRequest))
                .headers(getHttpHeadersAsConsumer(httpHeaders))
                .retrieve()
                .bodyToMono(SafJsonJournalpost.class)
                .doOnError(t -> handleError(t, "saf graphql (hent journalpost info)"))
                .block();

        if (respons == null || respons.getData() == null || respons.getJournalpost() == null) {
            throw new ClientErrorNotFoundException("Ingen journalpost ble funnet", ErrorCode.SAF_NOT_FOUND);
        }

        return respons.getJournalpost();

    }

    private Consumer<HttpHeaders> getHttpHeadersAsConsumer(HttpHeaders httpHeaders) {
        return consumer -> {
            consumer.addAll(httpHeaders);
        };
    }

    private void handleError(Throwable error, String serviceName) {
        if (error instanceof WebClientResponseException responseException) {
            var statusCode = responseException.getStatusCode();
            var responseBody = responseException.getResponseBodyAsString();
            var errorMessage = String.format("Kall mot %s feilet (statuskode: %s). Body: %s", serviceName, statusCode, responseBody);

            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw new ClientErrorUnauthorizedException(errorMessage, responseException, ErrorCode.SAF_UNAUTHORIZED);
            }

            if (statusCode == HttpStatus.FORBIDDEN) {
                throw new ClientErrorForbiddenException(errorMessage, responseException, ErrorCode.SAF_FORBIDDEN);
            }

            if (statusCode.is4xxClientError()) {
                throw new ClientErrorException(errorMessage, responseException, ErrorCode.SAF_ERROR);
            }

            throw new ServerErrorException(errorMessage, responseException, ErrorCode.SAF_ERROR);

        }
    }

}
