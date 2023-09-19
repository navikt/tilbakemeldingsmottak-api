package no.nav.tilbakemeldingsmottak.util

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.*
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

@Configuration
@EnableConfigurationProperties(ClientConfigurationProperties::class)
class RestClientTemplateSupport(
    private val oAuth2AccessTokenService: OAuth2AccessTokenService,
    private val clientConfigurationProperties: ClientConfigurationProperties
) {

    private val MAX_FILE_SIZE = 300 * 1024 * 1024

    @Value("\${pdl.url}")
    lateinit var pdlUrl: String

    @Bean
    @Qualifier("arkivClient")
    @Scope("prototype")
    fun arkivClient(): WebClient {
        val clientProperties = clientConfigurationProperties.registration["arkiv"]
            ?: throw RuntimeException("Fant ikke konfigurering for arkiv")

        return buildWebClient(buildHttpClient(5000, 60, 60), clientProperties)
    }

    @Bean
    @Qualifier("eregClient")
    @Scope("prototype")
    fun eregClient(): WebClient {
        return buildWebClient(buildHttpClient(5000, 60, 60))
    }

    @Bean
    @Qualifier("norg2Client")
    @Scope("prototype")
    fun norg2Client(): WebClient {
        return buildWebClient(buildHttpClient(5000, 60, 60))
    }

    @Bean
    @Qualifier("oppgaveClient")
    @Scope("prototype")
    fun oppgaveClient(): WebClient {
        val clientProperties = clientConfigurationProperties.registration["oppgave"]
            ?: throw RuntimeException("Fant ikke konfigurering for oppgave")

        return buildWebClient(buildHttpClient(5000, 60, 60), clientProperties)
    }

    @Bean
    @Qualifier("safclient")
    @Scope("prototype")
    fun safClientRestTemplate(): WebClient {
        val clientProperties = clientConfigurationProperties.registration["saf-maskintilmaskin"]
            ?: throw RuntimeException("Fant ikke konfigurering for saf-maskintilmaskin")

        return buildWebClient(buildHttpClient(5000, 60, 60), clientProperties)
    }

    // Denne overskriver den genererte SpringConfiguration sin webClient (i target/generated-sources)
    // Akkurat nå støttes bare én graphql server, men det kan støttes flere ved å følge denne: https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_more_than_one_graphql_servers
    @Bean(name = ["webClient"])
    fun webClient(): WebClient {
        val clientProperties = clientConfigurationProperties.registration["pdl"]
            ?: throw RuntimeException("Fant ikke konfigurering for pdl")

        return buildWebClientWithUrl(buildHttpClient(5000, 60, 60), clientProperties, pdlUrl)
    }

    private fun buildHttpClient(connection_timeout: Int, readTimeout: Int, writeTimeout: Int): HttpClient {
        return HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connection_timeout)
            .doOnConnected { conn: Connection ->
                conn
                    .addHandler(ReadTimeoutHandler(readTimeout.toLong(), TimeUnit.SECONDS))
                    .addHandler(WriteTimeoutHandler(writeTimeout))
            }
    }

    private fun buildWebClient(httpClient: HttpClient, clientProperties: ClientProperties): WebClient {
        return WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs { configurer: ClientCodecConfigurer ->
                    configurer
                        .defaultCodecs()
                        .maxInMemorySize(MAX_FILE_SIZE)
                }
                .build())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(bearerTokenExchange(clientProperties))
            .build()
    }

    private fun buildWebClientWithUrl(
        httpClient: HttpClient,
        clientProperties: ClientProperties,
        url: String?
    ): WebClient {
        return WebClient.builder()
            .baseUrl(url!!)
            .defaultHeader("Content-Type", "application/json").defaultUriVariables(Collections.singletonMap("url", url))
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs { configurer: ClientCodecConfigurer ->
                    configurer
                        .defaultCodecs()
                        .maxInMemorySize(MAX_FILE_SIZE)
                }
                .build())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(bearerTokenExchange(clientProperties))
            .build()
    }

    private fun buildWebClient(httpClient: HttpClient): WebClient {
        return WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs { configurer: ClientCodecConfigurer ->
                    configurer
                        .defaultCodecs()
                        .maxInMemorySize(MAX_FILE_SIZE)
                }
                .build())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }

    private fun bearerTokenExchange(clientProperties: ClientProperties): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest: ClientRequest?, exchangeFunction: ExchangeFunction ->
            val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
            val filtered = ClientRequest.from(
                clientRequest!!
            )
                .headers { headers: HttpHeaders -> headers.setBearerAuth(response.accessToken) }
                .build()
            exchangeFunction.exchange(filtered)
        }
    }

    @Bean
    @Qualifier("basicclient")
    @Scope("prototype")
    fun eregClientRestTemplate(
        restTemplateBuilder: RestTemplateBuilder
    ): RestTemplate {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(20))
            .build()
    }

}
