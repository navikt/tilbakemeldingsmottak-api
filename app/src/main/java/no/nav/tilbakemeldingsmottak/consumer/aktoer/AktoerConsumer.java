package no.nav.tilbakemeldingsmottak.consumer.aktoer;

import static no.nav.tilbakemeldingsmottak.config.cache.CacheConfig.AKTOER_CACHE;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.consumer.aktoer.domain.IdentInfoForAktoer;
import no.nav.tilbakemeldingsmottak.exceptions.aktoer.AktoerTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.inject.Inject;
import java.util.Map;


@Slf4j
@Component
public class AktoerConsumer {

	@Inject
	@Qualifier("serviceuserclient")
	private WebClient webClient;

	private final String aktoerregisterurl;

	public AktoerConsumer(@Value("${aktoerregister.identer.url}") String aktoerregisterurl) {
		this.aktoerregisterurl = aktoerregisterurl;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentAktoerIdForIdent"}, percentiles = {0.5, 0.95}, histogram = true)
	@Retryable(include = AktoerTechnicalException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
	@Cacheable(AKTOER_CACHE)
	public Map<String, IdentInfoForAktoer>  hentAktoerIdForIdent(String ident) {
		return webClient
				.method(HttpMethod.GET)
				.uri(aktoerregisterurl+"/identer/?gjeldende=true&identgruppe=AktoerId")
				.contentType(APPLICATION_JSON)
				.accept(APPLICATION_JSON)
				.body(BodyInserters.fromValue(ident))
				.header("Nav-Personidenter", ident)
				.header("Nav-Consumer-Id", "Tilbakemeldingsmottak")
				.header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
				.retrieve()
				.onStatus(HttpStatus::is5xxServerError, statusResponse -> {
					log.error(String.format("Teknisk feil ved kall mot AktoerV2 hentIdentForAktoerId: %s", statusResponse.statusCode()));
					throw new AktoerTechnicalException(String.format("Teknisk feil ved kall mot AktoerV2 hentIdentForAktoerId: %s.", statusResponse
							.statusCode()), new RuntimeException("Kall mot arkivet feilet"));
				})
				.bodyToMono(new ParameterizedTypeReference<Map<String, IdentInfoForAktoer>>(){})
				.block();
	}

}
