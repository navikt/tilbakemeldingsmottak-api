package no.nav.tilbakemeldingsmottak.util

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.tilbakemeldingsmottak.config.Constants.HEADER_BEHANDLINGSNUMMER
import no.nav.tilbakemeldingsmottak.config.Constants.PDL_BEHANDLINGSNUMMER
import no.nav.tilbakemeldingsmottak.exceptions.ServerErrorException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.*
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
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
    @Qualifier("safclient")
    @Scope("prototype")
    fun safClientRestTemplate(): WebClient {
        val clientProperties = clientConfigurationProperties.registration["saf-maskintilmaskin"]
            ?: throw RuntimeException("Fant ikke konfigurering for saf-maskintilmaskin")

        return webclientBuilder(buildHttpClient(5000, 60, 60), clientProperties).build()
    }

    @Bean
    @Qualifier("pdlClient")
    @Scope("prototype")
    fun pdlClient(): GraphQLWebClient {
        val clientProperties = clientConfigurationProperties.registration["pdl"]
            ?: throw RuntimeException("Fant ikke konfigurering for pdl")

        return GraphQLWebClient(
            url = pdlUrl,
            builder = pdlWebclientBuilder(buildHttpClient(5000, 60, 60), clientProperties)
        )
    }

    private fun buildHttpClient(connection_timeout: Int, readTimeout: Int, writeTimeout: Int): HttpClient {
        return HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connection_timeout)
            .doOnConnected { conn: Connection ->
                conn
                    .addHandlerLast(ReadTimeoutHandler(readTimeout.toLong(), TimeUnit.SECONDS))
                    .addHandlerLast(WriteTimeoutHandler(writeTimeout))
            }
    }

    fun webclientBuilder(httpClient: HttpClient, clientProperties: ClientProperties): WebClient.Builder {
        return WebClient.builder()
            .exchangeStrategies(createExchangeStrategies())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(bearerTokenExchange(clientProperties))
    }

    fun pdlWebclientBuilder(httpClient: HttpClient, clientProperties: ClientProperties): WebClient.Builder {
        return WebClient.builder()
            .exchangeStrategies(createExchangeStrategies())
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(bearerTokenExchange(clientProperties))
            .defaultRequest { it.header(HEADER_BEHANDLINGSNUMMER, PDL_BEHANDLINGSNUMMER) }
    }

    private fun createExchangeStrategies(): ExchangeStrategies {
        return ExchangeStrategies.builder()
            .codecs { configurer: ClientCodecConfigurer -> configurer.defaultCodecs().maxInMemorySize(MAX_FILE_SIZE) }
            .build()
    }

    private fun bearerTokenExchange(clientProperties: ClientProperties): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest: ClientRequest?, exchangeFunction: ExchangeFunction ->
            val response = oAuth2AccessTokenService.getAccessToken(clientProperties)

            if (response?.accessToken == null) {
                throw ServerErrorException("Fikk ikke accessToken fra token exchange")
            }

            val filtered = ClientRequest.from(
                clientRequest!!
            )
                .headers { headers: HttpHeaders -> headers.setBearerAuth(response.accessToken as String) }
                .build()
            exchangeFunction.exchange(filtered)
        }
    }

}
