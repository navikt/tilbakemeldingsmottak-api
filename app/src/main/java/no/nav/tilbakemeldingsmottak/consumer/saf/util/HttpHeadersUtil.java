package no.nav.tilbakemeldingsmottak.consumer.saf.util;

import no.nav.tilbakemeldingsmottak.exceptions.saf.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpHeadersUtil {

    private static final String OIDC_TOKEN_PREFIX = "Bearer";

    private HttpHeadersUtil() {
    }

    public static HttpHeaders createAuthHeaderFromToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authorizationHeader == null || !OIDC_TOKEN_PREFIX.equalsIgnoreCase(authorizationHeader.split(" ")[0])) {
            throw new ValidationException("Authorization header må være på formen Bearer {token}");
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, OIDC_TOKEN_PREFIX + " " + authorizationHeader.split(" ")[1]);
        return headers;
    }

}
