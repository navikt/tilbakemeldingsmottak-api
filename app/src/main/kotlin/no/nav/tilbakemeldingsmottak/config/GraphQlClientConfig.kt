package no.nav.tilbakemeldingsmottak.config

import no.nav.tilbakemeldingsmottak.config.Constants.HEADER_BEHANDLINGSNUMMER
import no.nav.tilbakemeldingsmottak.config.Constants.PDL_BEHANDLINGSNUMMER
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
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
     * Setter opp et filter som kan brukes for pdl kall.
     * AuthorizedClientManager har logikken for å håndtere
     * både 'client_credentials' og 'jwt-bearer' grant types (obo-fly).
     */
    @Bean
    @Qualifier("pdlAuthMng")
    fun oauth2ExchangeFilterFunction(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): ServletOAuth2AuthorizedClientExchangeFilterFunction {
        val oauth2 = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2.setDefaultOAuth2AuthorizedClient(true)
        oauth2.setDefaultClientRegistrationId("pdl")
        return oauth2
    }

    /**
     * Setter opp et filter som kan brukes for saf kall.
     * AuthorizedClientManager har logikken for å håndtere
     * både 'client_credentials' og 'jwt-bearer' grant types (obo-flyt).
     */
    @Bean
    @Qualifier("safAuthMng")
    fun oauth2SafExchangeFilterFunction(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): ServletOAuth2AuthorizedClientExchangeFilterFunction {
        val oauth2 = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2.setDefaultOAuth2AuthorizedClient(true)
        oauth2.setDefaultClientRegistrationId("saf-obo")
        return oauth2
    }

    /**
     * WebClient for PDL
     */
    @Bean
    @Qualifier("pdlWebClient")
    fun pdlWebClient(@Qualifier("pdlAuthMng") oauth2Filter: ServletOAuth2AuthorizedClientExchangeFilterFunction): WebClient {
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
    fun safWebClient(@Qualifier("safAuthMng") oauth2Filter: ServletOAuth2AuthorizedClientExchangeFilterFunction): WebClient {
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

}
