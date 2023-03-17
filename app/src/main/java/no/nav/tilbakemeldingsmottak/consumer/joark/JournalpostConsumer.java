package no.nav.tilbakemeldingsmottak.consumer.joark;

import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostRequestTo;
import no.nav.tilbakemeldingsmottak.consumer.joark.domain.OpprettJournalpostResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.joark.OpprettJournalpostTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.net.URI;

@Slf4j
@Component
public class JournalpostConsumer {

	@Inject
	@Qualifier("arkivClient")
	private WebClient webClient;

	@Value("${Journalpost_v1_url}")
	private String journalpostUrl;

	private static final String FORSOEK_FERDIGSTILL = "?forsoekFerdigstill=true";


	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "opprettJournalpost"}, percentiles = {0.5, 0.95}, histogram = true)
	public OpprettJournalpostResponseTo opprettJournalpost(OpprettJournalpostRequestTo opprettJournalpostRequestTo) {
		if (log.isDebugEnabled()) {
			log.debug("Oppretter journalpost");
		}
		OpprettJournalpostResponseTo response = webClient
				.method(HttpMethod.POST)
				.uri(journalpostUrl + "/journalpost/" + FORSOEK_FERDIGSTILL)
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.body(BodyInserters.fromValue(opprettJournalpostRequestTo))
				.header("Nav-Callid", MDC.get(MDC_CALL_ID))
				.header("Nav-Consumer-Id", "srvtilbakemeldings")
				.retrieve()
				.onStatus(HttpStatus::isError, statusResponse -> {
					log.error(String.format("OpprettJournalpost feilet med statusKode=%s", statusResponse.statusCode()));
					if (statusResponse.statusCode().is5xxServerError()) {
						throw new OpprettJournalpostTechnicalException(String.format("OpprettJournalpost feilet teknisk med statusKode=%s.", statusResponse
								.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
					} else if (statusResponse.statusCode().is4xxClientError()) {
						throw new OpprettJournalpostFunctionalException(String.format("OpprettJournalpost feilet funksjonelt med statusKode=%s.", statusResponse
								.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
					}
					return Mono.error(new IllegalStateException(
							String.format("OpprettJournalpost feilet med statusKode=%s", statusResponse.statusCode())));

				})
				.bodyToMono(OpprettJournalpostResponseTo.class)
				.block();

		if (log.isDebugEnabled()) {
			log.debug("Journalpost med journalpostId={} opprettet", response.getJournalpostId());
		}

		return response;

	}
}