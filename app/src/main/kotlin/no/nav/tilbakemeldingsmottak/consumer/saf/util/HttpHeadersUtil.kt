package no.nav.tilbakemeldingsmottak.consumer.saf.util

import no.nav.tilbakemeldingsmottak.exceptions.ClientErrorException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

object HttpHeadersUtil {
    private const val OIDC_TOKEN_PREFIX = "Bearer"
    fun createAuthHeaderFromToken(authorizationHeader: String?): HttpHeaders {
        val headers = HttpHeaders()
        if (authorizationHeader == null || !OIDC_TOKEN_PREFIX.equals(
                authorizationHeader.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0], ignoreCase = true)
        ) {
            throw ClientErrorException("Authorization header må være på formen Bearer {token}")
        }
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add(
            HttpHeaders.AUTHORIZATION,
            "$OIDC_TOKEN_PREFIX " + authorizationHeader.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1])
        return headers
    }
}
