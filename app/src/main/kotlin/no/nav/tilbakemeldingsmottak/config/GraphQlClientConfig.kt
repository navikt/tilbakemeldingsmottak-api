package no.nav.tilbakemeldingsmottak.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class GraphQlClientConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val authorizedClientRepository: OAuth2AuthorizedClientService
) {

    @Value("\${pdl.url}")
    private lateinit var pdlUrl: String

    @Value("\${saf.graphql.url}")
    private lateinit var safUrl: String

    @Bean
    @Qualifier("pdlQlClient")
    @Scope("prototype")
    fun graphQlWebClient(): HttpGraphQlClient {

        val webClient = buildWebClient(5000, 60, "pdl")

        return HttpGraphQlClient.builder(webClient)
            .url(pdlUrl)
            .build()
    }


    @Bean
    @Qualifier("safQlClient")
    @Scope("prototype")
    fun graphQlWebSafClient(): HttpGraphQlClient {

        val webClient = buildWebClient(5000, 60, "saf-maskintilmaskin")

        return HttpGraphQlClient.builder(webClient)
            .url(safUrl)
            .build()
    }

    private fun buildWebClient(readTimeout: Int, writeTimeout: Int, target: String): WebClient {
        val manager = AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientRepository
        )

        return WebClient.builder()
            .filter { request, next ->
                // Ask Spring Security for a token for the "pdl" client registration
                val authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(target)
                    .principal("graphQlClient") // no user -> client_credentials
                    .build()

                val client = manager.authorize(authorizeRequest)

                if (client == null || client.accessToken == null) {
                    return@filter Mono.error(IllegalStateException("No OAuth2 access token for $target"))
                }

                val token = client.accessToken!!.tokenValue
                val newRequest = ClientRequest.from(request)
                    .header("Authorization", "Bearer $token")
                    .build()

                next.exchange(newRequest)
            }
            .build()
    }
}
