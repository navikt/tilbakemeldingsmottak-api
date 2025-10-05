package no.nav.tilbakemeldingsmottak.config

import org.slf4j.MDC
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager

class OAuth2ClientCredentialsInterceptor(
    private val clientManager: OAuth2AuthorizedClientManager,
    private val clientRegistrationId: String
) : ClientHttpRequestInterceptor {

    private val anonymousPrincipal = AnonymousAuthenticationToken(
        "key",
        "anonymous",
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
    )


    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val authorizeRequest = OAuth2AuthorizeRequest
            .withClientRegistrationId(clientRegistrationId)
            .principal(anonymousPrincipal)
            .build()

        val authorizedClient = clientManager.authorize(authorizeRequest)
            ?: throw IllegalStateException("Failed to authorize client: $clientRegistrationId")

        request.headers.setBearerAuth(authorizedClient.accessToken.tokenValue)
        request.headers.set(MDCConstants.HEADER_CALL_ID, MDC.get(MDCConstants.MDC_CALL_ID))
        request.headers.set(MDCConstants.MDC_CONSUMER_ID, MDC.get(MDCConstants.MDC_CONSUMER_ID) ?: "")
        return execution.execute(request, body)
    }
}
