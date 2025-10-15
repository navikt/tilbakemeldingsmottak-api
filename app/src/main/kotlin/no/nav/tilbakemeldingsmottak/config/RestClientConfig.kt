package no.nav.tilbakemeldingsmottak.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.client.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.endpoint.*
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig() {


    /**
     * Konfigurerer RestClient for HentDokument med 'jwt-bearer'-flyt.
     */
    @Bean
    @Qualifier("hentDokumentRestClient")
    fun hentDokumentRestClient(
        @Value("\${hentdokument.url}") hentDokumentUrl: String,
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository
    ): RestClient {

        val oauth2Interceptor =
            createOauth2Interceptor(authorizedClientManager, "saf-obo", clientRegistrationRepository)
        return RestClient.builder()
            .baseUrl(hentDokumentUrl)
            .requestInterceptor(oauth2Interceptor)
            .build()
    }


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
    @Qualifier("arkivRestClient")
    @Scope("prototype")
    fun arkivRestClient(
        @Value("\${Journalpost_v1_url}") journalpostUrl: String,
        timeouts: ClientHttpRequestFactory,
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository

    ): RestClient {

        val oauth2Interceptor = createOauth2Interceptor(authorizedClientManager, "arkiv", clientRegistrationRepository)
        return RestClient.builder()
            .baseUrl(journalpostUrl)
            .requestFactory(timeouts)
            .requestInterceptor(oauth2Interceptor)
            .build()
    }

    @Bean
    @Qualifier("oppgaveRestClient")
    @Scope("prototype")
    fun oppgaveRestClient(
        @Value("\${oppgave_oppgaver_url}") oppgaveUrl: String,
        timeouts: ClientHttpRequestFactory,
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository
    ): RestClient {

        val oauth2Interceptor = createOauth2Interceptor(
            authorizedClientManager, "oppgave", clientRegistrationRepository
        )
        return RestClient.builder()
            .baseUrl(oppgaveUrl)
            .requestFactory(timeouts)
            .requestInterceptor(oauth2Interceptor)
            .build()
    }


    @Bean
    @Qualifier("oppgaveOboRestClient")
    @Scope("prototype")
    fun oppgaveOboRestClient(
        @Value("\${oppgave_oppgaver_url}") oppgaveUrl: String,
        timeouts: ClientHttpRequestFactory,
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository
    ): RestClient {

        val oauth2Interceptor = createOauth2Interceptor(
            authorizedClientManager, "oppgave-obo", clientRegistrationRepository
        )
        return RestClient.builder()
            .baseUrl(oppgaveUrl)
            .requestFactory(timeouts)
            .requestInterceptor(oauth2Interceptor)
            .build()
    }

    /**
     * Privat hjelpemetode for å lage en gjenbrukbar interceptor.
     * Denne metoden fungerer for både 'jwt-bearer' (som krever en bruker-principal)
     * og 'client_credentials' (som ikke krever det).
     */
    private fun createOauth2Interceptor(
        authorizedClientManager: OAuth2AuthorizedClientManager,
        clientRegistrationId: String,
        clientRegistrationRepository: ClientRegistrationRepository
    ): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { request, body, execution ->
            val clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId)
                ?: throw IllegalStateException("Fant ikke klient-registrering for '$clientRegistrationId'.")

            val authorizeRequestBuilder = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)

            if (clientRegistration.authorizationGrantType == AuthorizationGrantType.CLIENT_CREDENTIALS) {
                // ✅ For machine-to-machine flow, just use a static principal name
                authorizeRequestBuilder.principal("m2m-service-account")
            } else {
                // ✅ For OBO (JWT-bearer), forward the current authenticated user
                val principal = SecurityContextHolder.getContext().authentication
                    ?: throw IllegalStateException("Ingen SecurityContext Authentication funnet for OBO flyt.")
                authorizeRequestBuilder.principal(principal)
            }

            val authorizeRequest = authorizeRequestBuilder.build()

            val authorizedClient = authorizedClientManager.authorize(authorizeRequest)
                ?: throw IllegalStateException(
                    "Kunne ikke autorisere klienten '$clientRegistrationId'. " +
                            "Sjekk konfigurasjon og grant-type."
                )

            request.headers.setBearerAuth(authorizedClient.accessToken.tokenValue)
            execution.execute(request, body)
        }
    }

    private fun timeouts(
        readTimeoutMinutes: Long
    ): ClientHttpRequestFactory {
        val factory =
            JdkClientHttpRequestFactory() // Merk at SimpleClientHttpRequestFactory ikke har støtte for patch requests
        factory.setReadTimeout(Duration.ofMinutes(readTimeoutMinutes))
        return factory
    }

}