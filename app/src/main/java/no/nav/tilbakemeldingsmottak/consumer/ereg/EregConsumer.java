package no.nav.tilbakemeldingsmottak.consumer.ereg;

import static java.lang.String.format;
import static no.nav.tilbakemeldingsmottak.config.MDCConstants.MDC_CALL_ID;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import no.nav.tilbakemeldingsmottak.exceptions.ereg.EregFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.ereg.EregTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @author Sigurd Midttun, Visma Consulting.
 */
@Component
public class EregConsumer implements Ereg {

	private final RestTemplate restTemplate;
	private final String eregApiUrl;

	public EregConsumer(RestTemplateBuilder restTemplateBuilder,
						@Value("${ereg.api.url}") String eregApiUrl) {
		this.restTemplate = restTemplateBuilder
				.setReadTimeout(Duration.ofSeconds(20))
				.setConnectTimeout(Duration.ofSeconds(5))
				.build();
		this.eregApiUrl = eregApiUrl;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "eregHentInfo"}, percentiles = {0.5, 0.95}, histogram = true)
	public String hentInfo(String orgnr) {
		try {
			final String orgnrTrimmed = orgnr.trim();
			HttpHeaders headers = createHeaders();
			return restTemplate.exchange(eregApiUrl + "/v1/organisasjon/" + orgnrTrimmed,
					HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
		} catch (HttpClientErrorException e) {
			throw new EregFunctionalException(format("Funksjonell feil ved kall mot ereg:hentNoekkelinfo for organisasjonsnummer=%s. feilmelding=%s",
					orgnr, e.getMessage()), e);
		} catch (HttpServerErrorException e) {
			throw new EregTechnicalException(format("Teknisk feil ved kall mot ereg:hentNoekkelinfo for organisasjonsnummer=%s. Feilmelding=%s",
					orgnr, e.getMessage()), e);
		}
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Nav-Consumer-Id", "Tilbakemeldingsmottak");
		headers.add("Nav-Call-Id", MDC.get(MDC_CALL_ID));
		return headers;
	}
}
