package no.nav.tilbakemeldingsmottak.config

import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.client.*
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig(
    private val authorizedClientManager: OAuth2AuthorizedClientManager
) {


    @Bean
    @Qualifier("eregRestClient")
    @Scope("prototype")
    fun eregRestClient(
        @Value("\${ereg.api.url}") eregApiUrl: String
    ): RestClient {

        // Løses setting av Mdc verdier med WebMvcConfigurer?
        val mdcInterceptor = ClientHttpRequestInterceptor { request, body, execution ->
            val callId = MDC.get(MDCConstants.MDC_CALL_ID)
            val consumerId = MDC.get(MDCConstants.MDC_CONSUMER_ID) ?: ""

            callId?.let { request.headers.add(MDCConstants.HEADER_CALL_ID, it) }
            request.headers.add(MDCConstants.MDC_CONSUMER_ID, consumerId)

            execution.execute(request, body)
        }

        return RestClient.builder()
            .baseUrl(eregApiUrl)
            .requestFactory(
                timeouts(
                    readTimeoutMinutes = 1L
                )
            )
            .requestInterceptor(mdcInterceptor)
            .build()
    }

    @Bean
    @Qualifier("norg2RestClient")
    @Scope("prototype")
    fun norg2RestClient(
        @Value("\${norg2.api.v1.url}") norg2Url: String
    ): RestClient {

        val mdcInterceptor = ClientHttpRequestInterceptor { request, body, execution ->
            val callId = MDC.get(MDCConstants.MDC_CALL_ID)
            val consumerId = MDC.get(MDCConstants.MDC_CONSUMER_ID) ?: ""

            callId?.let { request.headers.add(MDCConstants.HEADER_CALL_ID, it) }
            request.headers.add(MDCConstants.MDC_CONSUMER_ID, consumerId)

            execution.execute(request, body)
        }

        return RestClient.builder()
            .baseUrl(norg2Url)
            .requestFactory(
                timeouts(
                    readTimeoutMinutes = 1L
                )
            )
            .requestInterceptor(mdcInterceptor)
            .build()
    }


    @Bean
    @Qualifier("hentDokumentRestClient")
    @Scope("prototype")
    fun hentDokumentRestClient(
        @Value("\${hentdokument.url}") hentDokcumentUrl: String,
        timeouts: ClientHttpRequestFactory
    ): RestClient {

        return RestClient.builder()
            .baseUrl(hentDokcumentUrl)
            .requestFactory(timeouts)
            .requestInterceptor(OAuth2ClientCredentialsInterceptor(authorizedClientManager, "saf-maskintilmaskin"))
            .build()
    }

    private fun timeouts(
        readTimeoutMinutes: Long
    ): ClientHttpRequestFactory {
        val factory =
            JdkClientHttpRequestFactory() // Merk at SimpleClientHttpRequestFactory ikke har støtte for patch requests
        factory.setReadTimeout(Duration.ofMinutes(readTimeoutMinutes))
        return factory
    }

    @Bean
    @Qualifier("arkivRestClient")
    @Scope("prototype")
    fun arkivRestClient(
        @Value("\${Journalpost_v1_url}") journalpostUrl: String,
        timeouts: ClientHttpRequestFactory
    ): RestClient {

        return RestClient.builder()
            .baseUrl(journalpostUrl)
            .requestFactory(timeouts)
            .requestInterceptor(OAuth2ClientCredentialsInterceptor(authorizedClientManager, "arkiv"))
            .build()
    }

    @Bean
    @Qualifier("oppgaveRestClient")
    @Scope("prototype")
    fun oppgaveRestClient(
        @Value("\${oppgave_oppgaver_url}") oppgaveUrl: String,
        timeouts: ClientHttpRequestFactory
    ): RestClient {

        return RestClient.builder()
            .baseUrl(oppgaveUrl)
            .requestFactory(timeouts)
            .requestInterceptor(OAuth2ClientCredentialsInterceptor(authorizedClientManager, "oppgave"))
            .build()
    }

}