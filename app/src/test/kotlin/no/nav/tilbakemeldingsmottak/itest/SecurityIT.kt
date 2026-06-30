package no.nav.tilbakemeldingsmottak.itest

import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.jwt.Jwt

internal class SecurityIT : ApplicationTest() {
    @Value("\${auth.issuers.azuread.issuer-uri}")
    lateinit var azureIssuer: String

    @Test
    fun `swagger ui is accessible without authentication`() {
        restTemplate!!
            .get()
            .uri("/swagger-ui/index.html")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `openapi docs are accessible without authentication and expose bearer auth`() {
        restTemplate!!
            .get()
            .uri("/v3/api-docs")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.components.securitySchemes.bearerAuth.type").isEqualTo("http")
            .jsonPath("$.security[0].bearerAuth").exists()
    }

    @Test
    fun `protected endpoint requires authentication when auth is enabled`() {
        restTemplate!!
            .get()
            .uri("/test/security/claim")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `claim protected endpoint accepts local azuread token with required scope`() {
        val mockJwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .claim("iss", azureIssuer)
            .claim("aud", "aud-localhost")
            .claim("sub", SAKSBEHANDLER)
            .claim("scp", "defaultaccess serviceklage-klassifisering")
            .build()
        `when`(azureJwtDecoder.decode(anyString())).thenReturn(mockJwt)

        restTemplate!!
            .get()
            .uri("/test/security/claim")
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer ${getToken(azureIssuer, SAKSBEHANDLER, "serviceklage-klassifisering")}"
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("ok")
    }
}
