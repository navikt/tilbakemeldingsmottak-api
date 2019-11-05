package no.nav.tilbakemeldingsmottak.consumer.sts;

import static no.nav.tilbakemeldingsmottak.util.RestSecurityHeadersUtils.createHeadersWithBasicAuth;

import no.nav.tilbakemeldingsmottak.exceptions.sts.StsFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.sts.StsTechnicalException;
import no.nav.tilbakemeldingsmottak.integration.fasit.ServiceuserAlias;
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

@Component
public class STSRestConsumer {

	private final RestTemplate restTemplate;
	private final String stsUrl;
	private final ServiceuserAlias serviceuserAlias;

	@Inject
	public STSRestConsumer(RestTemplate restTemplate, @Value("${security_token_service_token.url}") String stsUrl, ServiceuserAlias serviceuserAlias) {
		this.restTemplate = restTemplate;
		this.stsUrl = stsUrl;
		this.serviceuserAlias = serviceuserAlias;
	}

	public ResponseEntity<STSResponse> getServiceuserOIDCToken() {
		try {
			HttpHeaders httpHeaders = createHeadersWithBasicAuth(serviceuserAlias.getUsername(), serviceuserAlias.getPassword());
			return restTemplate.exchange(stsUrl + "?grant_type=client_credentials&scope=openid",  HttpMethod.GET, new HttpEntity<>(httpHeaders), STSResponse.class);
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
