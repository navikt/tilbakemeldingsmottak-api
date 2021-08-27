package no.nav.tilbakemeldingsmottak.consumer.saf.graphql;

import static no.nav.tilbakemeldingsmottak.consumer.saf.util.HttpHeadersUtil.createAuthHeaderFromToken;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJournalpostTo;
import no.nav.tilbakemeldingsmottak.consumer.saf.journalpost.SafJsonJournalpost;
import no.nav.tilbakemeldingsmottak.exceptions.oppgave.OpprettOppgaveTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafJournalpostIkkeFunnetFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafJournalpostQueryTechnicalException;
import no.nav.tilbakemeldingsmottak.exceptions.saf.SafJournalpostQueryUnauthorizedException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.function.Consumer;

@Component
@Slf4j
public class SafGraphqlConsumer {

	@Inject
	@Qualifier("safclient")
	private WebClient webClient;

	private final String graphQLurl;

	@Inject
	public SafGraphqlConsumer(@Value("${saf.graphql.url}") String graphQLurl) {
		this.graphQLurl = graphQLurl;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "safJournalpostquery"}, percentiles = {0.5, 0.95}, histogram = true)
	@Retryable(include = SafJournalpostQueryTechnicalException.class, maxAttempts = 3, backoff = @Backoff(delay = 500))
	public SafJournalpostTo performQuery(GraphQLRequest graphQLRequest, String authorizationHeader) {

		HttpHeaders httpHeaders = createAuthHeaderFromToken(authorizationHeader);
		SafJsonJournalpost respons =  webClient
				.method(HttpMethod.POST)
				.uri(graphQLurl)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.body(BodyInserters.fromValue(graphQLRequest))
				.headers(getHttpHeadersAsConsumer(httpHeaders))
				.retrieve()
				.onStatus(HttpStatus::isError, statusResponse -> {
					log.error(String.format("Query mot SAF tjenesten feilet med statusKode=%s", statusResponse.statusCode()));
					if (statusResponse.statusCode().is5xxServerError()) {
						throw new OpprettOppgaveTechnicalException(String.format("Tjenesten SAF (graphQL) feilet med status: %s.", statusResponse
								.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
					} else if (statusResponse.statusCode().is4xxClientError()) {
						throw new SafJournalpostQueryUnauthorizedException(String.format("Henting av journalpost feilet med status: %s.", statusResponse
								.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
					}
					return Mono.error(new IllegalStateException(
							String.format("Query mot SAF tjenesten feilet med statusKode=%s", statusResponse.statusCode())));

				})
				.bodyToMono(SafJsonJournalpost.class)
				.block();

		if (respons == null || respons.getData() == null || respons.getJournalpost() == null) {
			throw new SafJournalpostIkkeFunnetFunctionalException("Ingen journalpost ble funnet");
		}
		return respons.getJournalpost();

	}

	private Consumer<HttpHeaders> getHttpHeadersAsConsumer(HttpHeaders httpHeaders) {
		return consumer -> {
			consumer.addAll(httpHeaders);
		};
	}

}
