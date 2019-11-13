package no.nav.tilbakemeldingsmottak.consumer.aktoer;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import no.nav.tilbakemeldingsmottak.consumer.aktoer.domain.IdentInfoForAktoer;
import no.nav.tilbakemeldingsmottak.exceptions.aktoer.AktoerTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.util.RestSecurityHeadersUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Component
public class AktoerConsumer {
	private final RestTemplate restTemplate;
	private final String aktoerregisterurl;
	private final RestSecurityHeadersUtils restSecurityHeadersUtils;

	public AktoerConsumer(RestTemplate restTemplate, @Value("${aktoerregister.identer.url}") String aktoerregisterurl, RestSecurityHeadersUtils restSecurityHeadersUtils) {
		this.restTemplate = restTemplate;
		this.aktoerregisterurl = aktoerregisterurl;
		this.restSecurityHeadersUtils = restSecurityHeadersUtils;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "hentAktoerIdForIdent"}, percentiles = {0.5, 0.95}, histogram = true)
	@Retryable(include = AktoerTechnicalException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
	public Map<String, IdentInfoForAktoer>  hentAktoerIdForIdent(String ident) {
		try {
			HttpHeaders headers = restSecurityHeadersUtils.createOidcHeaders();
            headers.add("Nav-Personidenter", ident);
			headers.add("Nav-Consumer-Id", "Tilbakemeldingsmottak");
			headers.add("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID));
			ResponseEntity<Map<String, IdentInfoForAktoer>> responseEntity = restTemplate.exchange(aktoerregisterurl+"/identer/?gjeldende=true&identgruppe=AktoerId", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<Map<String, IdentInfoForAktoer>>(){});
			return responseEntity.getBody();
		} catch (Exception e) {
			throw new AktoerTechnicalException(String.format("Teknisk feil ved kall mot AktoerV2 hentIdentForAktoerId: %s", e
					.getMessage()), e);
		}
	}


}
