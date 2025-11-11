package no.nav.tilbakemeldingsmottak.config

import no.nav.tilbakemeldingsmottak.config.Constants.HEADER_BEHANDLINGSNUMMER
import no.nav.tilbakemeldingsmottak.config.Constants.PDL_BEHANDLINGSNUMMER
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration


@Configuration
class GraphQlClientConfig(
) {

    @Value("\${pdl.url}")
    private lateinit var pdlUrl: String

    @Value("\${saf.graphql.url}")
    private lateinit var safUrl: String

    private val responseTimeout = Duration.ofSeconds(15)

    /**
     * WebClient for PDL
     */
    @Bean
    @Qualifier("pdlWebClient")
    fun pdlWebClient(
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository
    ): WebClient {

        val oauth2Filter = oauth2ExchangeFilter(authorizedClientManager, clientRegistrationRepository, "pdl")

        val httpClient = HttpClient.create()
            .responseTimeout(responseTimeout)
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(oauth2Filter)
            .build()
    }

    /**
     * WebClient for SAF.
     */
    @Bean
    @Qualifier("safWebClient")
    fun safWebClient(
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository
    ): WebClient {

        val oauth2Filter = oauth2ExchangeFilter(authorizedClientManager, clientRegistrationRepository, "saf-obo")

        val httpClient = HttpClient.create()
            .responseTimeout(responseTimeout)
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(oauth2Filter)
            .build()
    }

    /**
     * HttpGraphQlClient for PDL.
     */
    @Bean
    @Qualifier("pdlQlClient")
    fun pdlGraphQlWebClient(@Qualifier("pdlWebClient") pdlWebClient: WebClient): HttpGraphQlClient {
        return HttpGraphQlClient.builder(pdlWebClient)
            .url(pdlUrl)
            .header(HEADER_BEHANDLINGSNUMMER, PDL_BEHANDLINGSNUMMER)
            .build()
    }

    /**
     * HttpGraphQlClient for SAF.
     */
    @Bean
    @Qualifier("safQlClient")
    fun safGraphQlWebClient(@Qualifier("safWebClient") safWebClient: WebClient): HttpGraphQlClient {
        return HttpGraphQlClient.builder(safWebClient)
            .url(safUrl)
            .build()
    }

    private fun oauth2ExchangeFilter(
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository,
        clientRegistrationId: String
    ): ExchangeFilterFunction {
        return ExchangeFilterFunction { request, next ->
            val clientRegistration =
                clientRegistrationRepository.findByRegistrationId(clientRegistrationId)
                    ?: throw IllegalStateException("Fant ikke client registration for '$clientRegistrationId'")

            // Velg principal-verdien, men hold typen trygg
            val principalString: String?
            val principalAuth: Authentication?

            when {
                clientRegistration.authorizationGrantType == AuthorizationGrantType.CLIENT_CREDENTIALS -> {
                    principalString = "system-service-account"
                    principalAuth = null
                }

                clientRegistration.authorizationGrantType == AuthorizationGrantType("urn:ietf:params:oauth:grant-type:jwt-bearer") -> {
                    principalAuth = SecurityContextHolder.getContext().authentication
                        ?: throw IllegalStateException("Ingen SecurityContext Authentication funnet for OBO-flyt.")
                    principalString = null
                }

                else -> throw IllegalStateException("Grant type '${clientRegistration.authorizationGrantType.value}' støttes ikke.")
            }

            // Bygg authorize-request med riktig overload (typetrygg)
            val authorizeRequest = if (principalAuth != null) {
                // OBO: bruk Authentication og sett subject_token som attribute om nødvendig
                OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                    .principal(principalAuth)
                    .attributes { attrs ->
                        // Legg på subject_token hvis din provider/authorizedClientProvider forventer det.
                        val jwt = (principalAuth as? JwtAuthenticationToken)?.token?.tokenValue
                        if (!jwt.isNullOrBlank()) {
                            attrs["subject_token"] = jwt
                        }
                    }
                    .build()
            } else {
                // client_credentials: bruk String principalName-overload
                OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                    .principal(principalString!!)
                    .build()
            }

            val authorizedClient = authorizedClientManager.authorize(authorizeRequest)
                ?: throw IllegalStateException("Kunne ikke autorisere klienten '$clientRegistrationId'.")

            val mutatedRequest = ClientRequest.from(request)
                .headers { it.setBearerAuth(authorizedClient.accessToken.tokenValue) }
                .build()

            next.exchange(mutatedRequest)
        }
    }
}
