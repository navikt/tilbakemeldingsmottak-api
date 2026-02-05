package no.nav.tilbakemeldingsmottak

import com.nimbusds.jose.JOSEObjectType
import com.ninjasquad.springmockk.MockkSpyBean
import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.security.token.support.spring.test.MockLoginController
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailClient
import no.nav.tilbakemeldingsmottak.config.TokenExchangeService
import no.nav.tilbakemeldingsmottak.repository.HendelseRepository
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository
import no.nav.tilbakemeldingsmottak.util.Api
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureDataSourceInitialization

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.cache.annotation.EnableCaching
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import java.util.*

@ActiveProfiles("itest")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    classes = [Application::class],
)
@ExtendWith(
    SpringExtension::class
)
@AutoConfigureDataSourceInitialization
@Transactional
@EnableMockOAuth2Server
@AutoConfigureWebTestClient
@EnableResilientMethods(proxyTargetClass = true)
@EnableCaching
class ApplicationTest {

    // Vi mocker ut de konkrete JwtDecoder-bønnene som er definert i SecurityConfig.
    // Dette er nøkkelen til å omgå den eksterne JWKS-valideringen i tester.
    @MockitoBean
    protected lateinit var azureJwtDecoder: JwtDecoder

    @MockitoBean
    protected lateinit var tokenxJwtDecoder: JwtDecoder

    @Autowired
    protected var serviceklageRepository: ServiceklageRepository? = null

    @MockitoBean
    private lateinit var serviceklagerBigQuery: ServiceklagerBigQuery

    @Autowired
    protected var hendelseRepository: HendelseRepository? = null

    @Autowired
    var restTemplate: WebTestClient? = null

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    lateinit var metricsRegistery: MeterRegistry

    @Autowired
    protected lateinit var tokenExchangeService: TokenExchangeService

    @Autowired
    protected lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @MockkSpyBean
    lateinit var aadMailClient: AADMailClient

    private val INNLOGGET_BRUKER = "14117119611"
    private val AUD = "aud-localhost"

    var api: Api? = null

    protected val SAKSBEHANDLER = "Saksbehandler"


    fun createMockJwt(issuer: String, subject: String = INNLOGGET_BRUKER): Jwt {
        return Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .claim("iss", issuer)
            .claim("aud", AUD)
            .claim("sub", subject)
            .claim("pid", subject)
            .build()
    }

    fun createMockJwt(issuer: String): Jwt {
        return Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .claim("iss", issuer)
            .claim("aud", AUD)
            .build()
    }

    fun createHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${getToken(AZURE_ISSUER, INNLOGGET_BRUKER)}")
        headers.add("correlation_id", UUID.randomUUID().toString())
        return headers
    }

    fun createHeaders(issuer: String, user: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${getToken(issuer, user)}")
        headers.add("correlation_id", UUID.randomUUID().toString())
        return headers
    }

    fun createHeaders(issuer: String, user: String, loggedIn: Boolean): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add("correlation_id", UUID.randomUUID().toString())
        if (loggedIn) {
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${getToken(issuer, user)}")
        } else {
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${getTokenWithOutPid(issuer, user)}")
        }
        return headers
    }

    fun createHeaders(issuer: String, user: String, scope: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ${getToken(issuer, user, scope)}")
        headers.add("correlation_id", UUID.randomUUID().toString())
        return headers
    }

    private fun getToken(user: String): String {
        return token(AZURE_ISSUER, user, AUD)
    }

    fun getToken(issuer: String, user: String): String {
        return tokenWithClaims(issuer, user, AUD, mapOf("acr" to "Level4", "pid" to user))
    }

    fun getToken(issuer: String, user: String, scope: String): String {
        return tokenWithClaims(issuer, user, AUD, mapOf("scp" to "defaultaccess $scope"))
    }

    fun getTokenWithOutPid(issuer: String, user: String): String {
        return token(issuer, user, AUD)
    }

    private fun token(issuerId: String, subject: String, audience: String): String {
        val oAuth2TokenCallback = DefaultOAuth2TokenCallback(
            issuerId,
            subject,
            JOSEObjectType.JWT.type,
            listOf(audience),
            mapOf("acr" to "Level4"),
            3600
        )
        return mockOAuth2Server.issueToken(
            issuerId,
            MockLoginController::class.java.simpleName,
            oAuth2TokenCallback
        ).serialize()
    }

    private fun tokenWithClaims(
        issuerId: String,
        subject: String,
        audience: String,
        claims: Map<String, String>
    ): String {
        val oAuth2TokenCallback = DefaultOAuth2TokenCallback(
            issuerId,
            subject,
            JOSEObjectType.JWT.type,
            listOf(audience),
            claims,
            3600
        )
        return mockOAuth2Server.issueToken(
            issuerId,
            MockLoginController::class.java.simpleName,
            oAuth2TokenCallback
        ).serialize()
    }

}
