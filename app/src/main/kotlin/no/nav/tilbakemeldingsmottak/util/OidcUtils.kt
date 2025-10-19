package no.nav.tilbakemeldingsmottak.util

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class OidcUtils {

    private val log = LoggerFactory.getLogger(OidcUtils::class.java)

    fun currentJwt(): Jwt? {
        val auth = SecurityContextHolder.getContext().authentication
        return (auth as? JwtAuthenticationToken)?.token
    }

    fun getSubject(): String? =
        currentJwt()?.subject

    fun getEmail(): String? =
        currentJwt()?.claims?.get("preferred_username") as? String

    fun getPid(): String? =
        currentJwt()?.claims?.get("pid") as? String

    fun getIssuer(): String? =
        currentJwt()?.claims?.get("iss") as? String

    fun getClaim(claimName: String): Any? =
        currentJwt()?.claims?.get(claimName)

    fun isLoggedIn(isInternal: Boolean): Boolean =
        if (getPid() != null)
            true
        else
            false

    fun logClaims() {
        val jwt = currentJwt()
        if (jwt == null) {
            log.info("No JWT in security context")
        } else {
            log.info("JWT issuer=${jwt.issuer}, subject=${jwt.subject}, claims=${jwt.claims.keys}")
        }
    }

    /**
     * Returns the raw Bearer token string (for forwarding to downstream services if necessary).
     */
    fun getRawTokenValue(): String? =
        currentJwt()?.tokenValue
}
