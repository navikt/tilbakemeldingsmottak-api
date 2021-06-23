package no.nav.tilbakemeldingsmottak.consumer.sts;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_CONSUMER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import no.nav.tilbakemeldingsmottak.exceptions.sts.StsFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.sts.StsTechnicalException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URL;

@Component
public class STSRestConsumer {

	@Inject
	@Qualifier("basicauthclient")
	private RestTemplate restTemplate;
	private final String stsUrl;

	@Inject
	public STSRestConsumer(@Value("${security_token_service_token.url}") String stsUrl) {
		this.stsUrl = stsUrl;
	}

	@Metrics(value = DOK_CONSUMER, extraTags = {PROCESS_CODE, "getServiceuserOIDCToken"}, percentiles = {0.5, 0.95}, histogram = true)
	public ResponseEntity<STSResponse> getServiceuserOIDCToken() {
		try {
			return restTemplate.getForEntity(stsUrl + "?grant_type=client_credentials&scope=openid", STSResponse.class);
/*
			HttpHeaders httpHeaders = restTemplate.headForHeaders(stsUrl);
			return restTemplate.exchange(stsUrl + "?grant_type=client_credentials&scope=openid",  HttpMethod.GET, new HttpEntity<>(httpHeaders), STSResponse.class);
*/
		} catch (
				HttpClientErrorException e) {
			throw new StsTechnicalException(String.format("Kallet til STS feilet med status=%s feilmelding=%s.", e.getStatusCode(), e.getMessage()), e);
		} catch (
				HttpServerErrorException e) {
			throw new StsFunctionalException(String.format("Kallet til STS feilet teknisk med status=%s feilmelding=%s", e
					.getStatusCode(), e.getMessage()), e);
		}
	}
}
