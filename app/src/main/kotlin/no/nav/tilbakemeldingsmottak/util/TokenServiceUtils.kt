package no.nav.tilbakemeldingsmottak.util

import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService

class TokenServiceUtils(
    private val clientProperties: ClientProperties,
    private val oauth2TokenService: OAuth2AccessTokenService
) {
    fun getToken(): String? = oauth2TokenService.getAccessToken(clientProperties).accessToken
}
