package no.nav.tilbakemeldingsmottak

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.ContentTypeHeader
import com.nimbusds.jose.JOSEObjectType
import io.micrometer.core.instrument.MeterRegistry
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.security.token.support.spring.test.MockLoginController
import no.nav.tilbakemeldingsmottak.TestUtils.createNorg2Response
import no.nav.tilbakemeldingsmottak.TestUtils.createSafGraphqlResponse
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER
import no.nav.tilbakemeldingsmottak.config.TokenExchangeService
import no.nav.tilbakemeldingsmottak.repository.HendelseRepository
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository
import no.nav.tilbakemeldingsmottak.util.Api
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.ldap.AutoConfigureDataLdap
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.test.web.servlet.MockMvc
import java.time.Instant
import java.util.*

@ActiveProfiles("itest")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    classes = [Application::class],
)
//@EnableAutoConfiguration
@ExtendWith(
    SpringExtension::class
)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@AutoConfigureDataLdap
@Transactional
@EnableMockOAuth2Server(port = 1888)
@AutoConfigureWireMock(port = 5490)
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    // SecurityMockMvcRequestPostProcessors

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
    protected var restTemplate: WebTestClient? = null

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

    @Value("\${local.server.port}")
    protected val serverPort = 5490

    private val INNLOGGET_BRUKER = "14117119611"
    private val AUD = "aud-localhost"

    var api: Api? = null

    protected val SAKSBEHANDLER = "Saksbehandler"

    @BeforeEach
    fun setup() {

        api = Api(restTemplate!!)
        hendelseRepository!!.deleteAll()
        serviceklageRepository!!.deleteAll()
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/OPPRETT_JOURNALPOST/journalpost/"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.CREATED.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("joark/opprettJournalpost/opprettJournalpostResponse.json")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/ereg/v1/organisasjon/[0-9]*"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("hei")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/ereg/v1/organisasjon/[0-9]*"))
                .inScenario("opprett_serviceklage").whenScenarioStateIs("ereg_404")
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("not_found")
                )
        )
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/OPPGAVE"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/opprettOppgaveResponse.json")
                )
        )
        WireMock.stubFor(
            WireMock.patch(WireMock.urlPathMatching("/OPPGAVE/[0-9]*"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("Oppgave endret")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/OPPGAVE/[8]*"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveIkkeEksisterendeJournalpostResponse.json")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/OPPGAVE/[9]*"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveIngenJournalpostSattResponse.json")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/OPPGAVE/[0-7]*"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("oppgave/hentOppgaveResponse.json")
                )
        )
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/pdlgraphql"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("pdl/hentIdenterResponse.json")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/norg2/enhet"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createNorg2Response())
                )
        )
        WireMock.stubFor(
            WireMock.post(WireMock.urlPathMatching("/safgraphql"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(createSafGraphqlResponse())
                    //.withBodyFile("saf/hentJournalpostResponse.json")

                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/hentdokument/.*/.*/.*"))
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")
                )
        )
        WireMock.stubFor(
            WireMock.get(WireMock.urlPathMatching("/hentdokument/.*/.*/.*"))
                .inScenario("hent_dokument").whenScenarioStateIs("saf_403")
                .willReturn(
                    WireMock.aResponse().withStatus(HttpStatus.FORBIDDEN.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{}")
                )
        )
        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("/fake/token"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                        {
                          "access_token": "mocked-obo-access-token",
                          "token_type": "Bearer",
                          "expires_in": 3600
                        }
                        """.trimIndent()
                        )
                )
        )

        metricsRegistery.clear()
    }

    @AfterEach
    fun tearDown() {
        WireMock.reset()
        WireMock.resetAllScenarios()
    }

    // Hjelpemetode for å lage et gyldig mock JWT-objekt for testing.
    fun createMockJwt(issuer: String, subject: String = INNLOGGET_BRUKER, scope: String): Jwt {
        return Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .claim("iss", issuer)
            .claim("aud", AUD)
            .claim("sub", subject)
            .claim("pid", subject)
            .claim("scp", scope)
            .build()
        // ha med claims: mapOf("acr" to "Level4")
    }

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
