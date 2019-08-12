package no.nav.tilbakemeldingsmottak.util;

import no.nav.tilbakemeldingsmottak.service.sts.STSTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Component
public final class RestSecurityHeadersUtils {

	private final STSTokenService stsTokenService;

	@Inject
	private RestSecurityHeadersUtils(STSTokenService stsTokenService) {
		this.stsTokenService = stsTokenService;
	}

    public HttpHeaders createOidcHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + stsTokenService.hentOidcToken().getAccessToken());
        return headers;
    }

	public static HttpHeaders createHeadersWithBasicAuth(String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(username, password);
		return headers;
	}
}
