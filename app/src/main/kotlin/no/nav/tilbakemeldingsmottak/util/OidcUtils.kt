package no.nav.tilbakemeldingsmottak.util

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OidcUtils(private val tokenValidationContextHolder: TokenValidationContextHolder) {
    private val log: Logger = LoggerFactory.getLogger(OidcUtils::class.java)

    fun getSubjectForIssuer(issuer: String): String? {
        val userToken = tokenValidationContextHolder.tokenValidationContext.getJwtToken(issuer)
        return try {
            userToken?.subject
        } catch (e: Exception) {
            throw RuntimeException("Feil i parsing av token (getSubjectForIssuer)", e)
        }
    }

    fun getEmailForIssuer(issuer: String): String? {
        val userToken = tokenValidationContextHolder.tokenValidationContext.getJwtToken(issuer)
        return try {
            userToken?.jwtTokenClaims?.getStringClaim("preferred_username")
        } catch (e: Exception) {
            throw RuntimeException("Feil i parsing av token (getEmailForIssuer)", e)
        }
    }

    fun getPidForIssuer(issuer: String): String? {
        val userToken = tokenValidationContextHolder.tokenValidationContext.getJwtToken(issuer)
        return try {
            val pid = userToken?.jwtTokenClaims?.getStringClaim("pid")
            pid.takeIf { it != null }
        } catch (e: Exception) {
            throw RuntimeException("Feil i parsing av token (getPidForIssuer)", e)
        }
    }

    fun getFirstValidToken(): String {
        return tokenValidationContextHolder.tokenValidationContext.firstValidToken?.get()?.tokenAsString
            ?: throw RuntimeException("Finner ikke validert OIDC-token")
    }

    fun getSubjectForFirstValidToken(): String? {
        return tokenValidationContextHolder.tokenValidationContext.firstValidToken?.get()?.subject
    }

    fun checkSubjectOnEachIssuer() {
        val issuers = tokenValidationContextHolder.tokenValidationContext.issuers
        issuers.forEach { issuer -> logClaims(issuer) }
    }

    private fun logClaims(issuer: String) {
        val userToken = tokenValidationContextHolder.tokenValidationContext.getJwtTokenAsOptional(issuer)
        log.info("Context issuer=$issuer User=${userToken.map { it.subject }.orElse("Ikke funnet")}")
    }

    fun getSubject(token: String?): String? {
        if (token != null) {
            val jwtToken = JwtToken(token)
            val pid = jwtToken.jwtTokenClaims.getStringClaim("pid")
            return pid ?: jwtToken.subject
        }
        return null
    }
}
