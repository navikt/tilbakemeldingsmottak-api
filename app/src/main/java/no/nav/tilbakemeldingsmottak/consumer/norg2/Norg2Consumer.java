package no.nav.tilbakemeldingsmottak.consumer.norg2;

import static java.lang.String.format;
import static no.nav.tilbakemeldingsmottak.config.cache.CacheConfig.NORG2_CACHE;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.exceptions.norg2.HentEnheterFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.norg2.HentEnheterTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.List;


@Component
public class Norg2Consumer {

	@Inject
	@Qualifier("basicclient")
	private RestTemplate restTemplate;

	private final String norg2Url;

	public Norg2Consumer(@Value("${norg2.api.v1.url}") String norg2Url) {
		this.norg2Url = norg2Url;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentEnheter"}, percentiles = {0.5, 0.95}, histogram = true)
	@Retryable(include = HentEnheterTechnicalException.class, backoff = @Backoff(delay = 1000))
	@Cacheable(NORG2_CACHE)
	public List<Enhet> hentEnheter() {
		try {
			return restTemplate.exchange(norg2Url + "/enhet",
					HttpMethod.GET, new HttpEntity<>(createHeaders()), new ParameterizedTypeReference<List<Enhet>>() {
					}).getBody();
		} catch (HttpClientErrorException e) {
			throw new HentEnheterFunctionalException(format("Funksjonell feil ved kall mot norg2:enheter (v1/enhet/). Feilmelding=%s",
					e.getMessage()), e);
		} catch (HttpServerErrorException e) {
			throw new HentEnheterTechnicalException(format("Teknisk feil ved kall mot norg2:enheter (v1/enhet/). Feilmelding=%s",
					e.getMessage()), e);
		}
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Nav-Consumer-Id", "tilbakemeldingsmottak");
		headers.add("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID));
		return headers;
	}
}
