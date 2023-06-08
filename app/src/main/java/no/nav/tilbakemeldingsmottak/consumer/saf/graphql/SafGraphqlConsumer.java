package no.nav.tilbakemeldingsmottak.consumer.saf.graphql;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost;
import no.nav.tilbakemeldingsmottak.exceptions.*;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .onStatus(HttpStatusCode::isError, statusResponse -> errorResponse(statusResponse, "saf graphql (hent journalpost info)"))
                .bodyToMono(SafJsonJournalpost.class)
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

    private Mono<Throwable> errorResponse(ClientResponse statusResponse, String tjeneste) {

        if (statusResponse.statusCode().is4xxClientError()) {
            if (statusResponse.statusCode().value() == 403 || statusResponse.statusCode().value() == 401) {
                return statusResponse.bodyToMono(String.class).flatMap(body ->
                        Mono.error(new ClientErrorUnauthorizedException(String.format("Autentisering mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.SAF_UNAUTHORIZED))
                );
            }

            log.error("Kall mot {} feilet med statuskode {}", tjeneste, statusResponse.statusCode());

            return statusResponse.bodyToMono(String.class).flatMap(body ->
                    Mono.error(new ClientErrorException(String.format("Kall mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.SAF_ERROR))
            );
        }

        return statusResponse.bodyToMono(String.class).flatMap(body ->
                Mono.error(new ServerErrorException(String.format("Kall mot %s feilet (statusCode: %s)", tjeneste, statusResponse.statusCode()), new RuntimeException(body), ErrorCode.SAF_ERROR))
        );
    }

}
