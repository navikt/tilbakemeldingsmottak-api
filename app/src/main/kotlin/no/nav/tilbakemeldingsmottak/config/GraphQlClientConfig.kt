package no.nav.tilbakemeldingsmottak.config

import no.nav.tilbakemeldingsmottak.config.Constants.HEADER_BEHANDLINGSNUMMER
import no.nav.tilbakemeldingsmottak.config.Constants.PDL_BEHANDLINGSNUMMER
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext
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

    /**
     * Setter opp et felles filter som kan brukes av alle WebClients.
     * Den auto-konfigurerte 'authorizedClientManager' har all logikken for å håndtere
     * både 'client_credentials' og 'jwt-bearer' grant types.
     */
    @Bean
    fun oauth2ExchangeFilterFunction(
        authorizedClientManager: OAuth2AuthorizedClientManager
    ): ServletOAuth2AuthorizedClientExchangeFilterFunction {
        val oauth2 = ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2.setDefaultOAuth2AuthorizedClient(true)

        return oauth2
    }

    /**
     * WebClient for PDL med 'client_credentials'-flyt.
     */
    @Bean
    @Qualifier("pdlWebClient")
    fun pdlWebClient(oauth2Filter: ServletOAuth2AuthorizedClientExchangeFilterFunction): WebClient {
        // Konfigurerer en HTTP-klient med 15 sekunders timeout
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(15))
        oauth2Filter.setDefaultClientRegistrationId("pdl")
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(oauth2Filter) // 1. Legg til selve filteret
            .defaultRequest { spec -> // 2. Sett en standard-attributt for alle kall
                // Dette forteller filteret hvilken klient-konfigurasjon det skal bruke
                spec.attributes { attrs ->
                    attrs[OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME] = "scope"
                }
            }
            .build()
    }

    /**
     * WebClient for SAF med 'jwt-bearer'-flyt.
     */
    @Bean
    @Qualifier("safWebClient")
    fun safWebClient(oauth2Filter: ServletOAuth2AuthorizedClientExchangeFilterFunction): WebClient {
        // Konfigurerer en HTTP-klient med 15 sekunders timeout
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(15))
        oauth2Filter.setDefaultClientRegistrationId("saf-obo")
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient)) // Legger til HTTP-klienten
            .filter(oauth2Filter) // 1. Legg til selve filteret
            .defaultRequest { spec -> // 2. Sett en standard-attributt for alle kall
                // Dette forteller filteret hvilken klient-konfigurasjon det skal bruke
                spec.attributes { attrs ->
                    attrs[OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME] = "scope"
                }
            }
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
