package no.nav.tilbakemeldingsmottak.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class OAuth2ClientManagerConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val tokenExchangeService: TokenExchangeService
) {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    @Bean
    fun authorizedClientManager(): OAuth2AuthorizedClientManager {
        // Register support for client_credentials and our custom jwt-bearer flow
        val provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .provider { context ->
                val grantType = context.clientRegistration.authorizationGrantType.value
                log.info("OAuth2AuthorizedClientProvider konfigurert med grantType: $grantType og registrationId: ${context.clientRegistration.registrationId}")
                if (grantType == "urn:ietf:params:oauth:grant-type:jwt-bearer") {
                    tokenExchangeService.performJwtBearerExchange(context)
                } else null
            }
            .build()

        val manager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
        )
        manager.setAuthorizedClientProvider(provider)
        log.info("OAuth2AuthorizedClientManager konfigurert.")
        return manager
    }
}
