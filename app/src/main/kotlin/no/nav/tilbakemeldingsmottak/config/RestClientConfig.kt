package no.nav.tilbakemeldingsmottak.config

import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.tilbakemeldingsmottak.util.TokenServiceUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpRequest
import org.springframework.http.client.*
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
@EnableConfigurationProperties(ClientConfigurationProperties::class)
class RestClientConfig {

    @Bean
    @Qualifier("arkivRestClient")
    @Scope("prototype")
    fun arkivRestClient(
        @Value("\${Journalpost_v1_url}") journalpostUrl: String,
        oAuth2AccessTokenService: OAuth2AccessTokenService,
        clientConfigurationProperties: ClientConfigurationProperties
    ): RestClient {

        return restClientOAuth2Client(
            baseUrl = journalpostUrl,
            timeouts = timeouts(
                readTimeoutMinutes = 1L
            ),
            clientAccessProperties = clientConfigurationProperties.registration["arkiv"]!!,
            oAuth2AccessTokenService = oAuth2AccessTokenService
        )
    }

    @Bean
    @Qualifier("eregRestClient")
    @Scope("prototype")
    fun eregRestClient(
        @Value("\${ereg.api.url}") eregApiUrl: String
    ): RestClient {

        return RestClient.builder()
            .baseUrl(eregApiUrl)
            .requestFactory(
                timeouts(
                    readTimeoutMinutes = 1L
                )
            )
            .build()
    }

    @Bean
    @Qualifier("norg2RestClient")
    @Scope("prototype")
    fun norg2RestClient(
        @Value("\${norg2.api.v1.url}") norg2Url: String
    ): RestClient {

        return RestClient.builder()
            .baseUrl(norg2Url)
            .requestFactory(
                timeouts(
                    readTimeoutMinutes = 1L
                )
            )
            .build()
    }

    @Bean
    @Qualifier("oppgaveRestClient")
    @Scope("prototype")
    fun oppgaveRestClient(
        @Value("\${oppgave_oppgaver_url}") oppgaveUrl: String,
        oAuth2AccessTokenService: OAuth2AccessTokenService,
        clientConfigurationProperties: ClientConfigurationProperties

    ): RestClient {

        return restClientOAuth2Client(
            baseUrl = oppgaveUrl,
            timeouts = timeouts(
                readTimeoutMinutes = 1L
            ),
            clientAccessProperties = clientConfigurationProperties.registration["oppgave"]!!,
            oAuth2AccessTokenService = oAuth2AccessTokenService
        )
    }


    private fun timeouts(
        readTimeoutMinutes: Long
    ): ClientHttpRequestFactory {
        val factory =
            JdkClientHttpRequestFactory() // Merk at SimpleClientHttpRequestFactory ikke har støtte for patch requests
        factory.setReadTimeout(Duration.ofMinutes(readTimeoutMinutes))
        return factory
    }

    private fun restClientOAuth2Client(
        baseUrl: String,
        timeouts: ClientHttpRequestFactory,
        clientAccessProperties: ClientProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ): RestClient {

        val tokenService = TokenServiceUtils(clientAccessProperties, oAuth2AccessTokenService)

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(timeouts)
            .requestInterceptor(RequestHeaderInterceptor(tokenService))
            .build()
    }

    class RequestHeaderInterceptor(val tokenService: TokenServiceUtils) :
        ClientHttpRequestInterceptor {

        val logger: Logger = LoggerFactory.getLogger(javaClass)

        override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            val token = tokenService.getToken()
            request.headers.setBearerAuth(token ?: "")
            request.headers.set(MDCConstants.MDC_CALL_ID, MDC.get(MDCConstants.MDC_CALL_ID))
            request.headers.set(MDCConstants.MDC_CONSUMER_ID, MDC.get(MDCConstants.MDC_CONSUMER_ID) ?: "")

            return execution.execute(request, body)
        }
    }

}