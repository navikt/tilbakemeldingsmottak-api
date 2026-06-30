package no.nav.tilbakemeldingsmottak.itest

import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.ninjasquad.springmockk.MockkSpyBean
import io.mockk.justRun
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import no.nav.security.token.support.spring.test.MockLoginController
import no.nav.tilbakemeldingsmottak.Application
import no.nav.tilbakemeldingsmottak.WireMockStubs
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailClient
import no.nav.tilbakemeldingsmottak.util.builders.OpprettServiceklageV2RequestBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.reactive.server.WebTestClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.util.UUID

/**
 * Black-box authentication test for issue #339.
 *
 * The application is booted as close to production as possible: real JWTs are issued by
 * MockOAuth2Server and validated end-to-end by the real `delegatingJwtDecoder` (the
 * `JwtDecoder` beans are NOT mocked). The `auth-prod-like-itest` profile gives Azure AD a
 * prod-style issuer URI (`/microsoft`) that does not contain the substring "azuread", which
 * is what exposed the broken `IssuerChecker` substring logic in production. Only external
 * services the app calls are mocked (WireMock for downstream HTTP, mail and BigQuery beans).
 */
@ActiveProfiles("itest", "auth-prod-like-itest")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    classes = [Application::class],
)
@ExtendWith(SpringExtension::class)
@EnableMockOAuth2Server
@AutoConfigureWebTestClient
internal class ProdLikeAuthIT {

    private val objectMapper = jacksonObjectMapper()
    private val aud = "aud-localhost"

    @MockitoBean
    private lateinit var serviceklagerBigQuery: ServiceklagerBigQuery

    @MockkSpyBean
    lateinit var aadMailClient: AADMailClient

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    companion object {
        @JvmField
        @RegisterExtension
        val wm: WireMockExtension = WireMockExtension.newInstance()
            .configureStaticDsl(true)
            .options(wireMockConfig().port(8099))
            .build()

        @JvmStatic
        @DynamicPropertySource
        fun properties(reg: DynamicPropertyRegistry) {
            val base = "http://localhost:${wm.port}"
            reg.add("spring.security.oauth2.client.provider.azuread.token-uri") { "$base/fake/token" }
            reg.add("Journalpost_v1_url") { "$base/OPPRETT_JOURNALPOST" }
            reg.add("oppgave_oppgaver_url") { "$base/OPPGAVE" }
        }
    }

    @BeforeEach
    fun setup() {
        wm.resetAll()
        WireMockStubs.stubTokenEndpoint()
        WireMockStubs.stubForJoark()
        WireMockStubs.stubForPdlHentIdenter()
        WireMockStubs.stubForOpprettOppgave()
        justRun { aadMailClient.sendMailViaClient(any()) }
    }

    private fun token(issuerId: String, subject: String, claims: Map<String, Any>): String =
        mockOAuth2Server.issueToken(
            issuerId,
            MockLoginController::class.java.simpleName,
            DefaultOAuth2TokenCallback(issuerId, subject, "JWT", listOf(aud), claims, 3600)
        ).serialize()

    private fun postServiceklageV2(token: String?): WebTestClient.ResponseSpec {
        val req = OpprettServiceklageV2RequestBuilder().asPrivatPerson().build()
        val spec = webTestClient.post()
            .uri("/rest/v2/serviceklage")
            .contentType(MediaType.APPLICATION_JSON)
            .header("correlation_id", UUID.randomUUID().toString())
        token?.let { spec.header(HttpHeaders.AUTHORIZATION, "Bearer $it") }
        return spec.bodyValue(objectMapper.writeValueAsString(req)).exchange()
    }

    @Test
    fun `M2M token from Azure AD is accepted (issue 339 regression)`() {
        val m2m = token("microsoft", "client-id", mapOf("scp" to "defaultaccess"))
        postServiceklageV2(m2m).expectStatus().value { code -> assert(code == 200) }
    }

    @Test
    fun `OBO token from TokenX is accepted`() {
        val obo = token("tokenx", "01010096460", mapOf("pid" to "01010096460", "acr" to "Level4"))
        postServiceklageV2(obo).expectStatus().value { code -> assert(code == 200) }
    }

    @Test
    fun `token with unknown issuer is rejected`() {
        val unknown = token("evil", "client-id", emptyMap())
        postServiceklageV2(unknown).expectStatus().isUnauthorized
    }

    @Test
    fun `missing token is rejected`() {
        postServiceklageV2(null).expectStatus().isUnauthorized
    }
}
