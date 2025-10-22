package no.nav.tilbakemeldingsmottak.config

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component("claimChecker")
class ClaimChecker {

    private val log = LoggerFactory.getLogger(javaClass)

    fun hasAccess(authentication: Authentication, requiredScopes: Collection<String>): Boolean {
        if (authentication !is JwtAuthenticationToken) {
            log.warn("Ugyldig authentication-type: ${authentication::class.java.simpleName}")
            return false
        }

        val jwt = authentication.token
        val issuer = jwt.issuer?.toString() ?: return false
        if (!issuer.contains("login.microsoftonline.com", ignoreCase = true)) {
            log.info("Avvist: issuer $issuer er ikke AzureAD")
            return false
        }

        val scopes: List<String> =
            when (val claim = jwt.claims["scp"] ?: jwt.claims["scope"]) {
                is String -> claim.split(" ")
                is Collection<*> -> claim.filterIsInstance<String>()
                else -> emptyList()
            }
        val harAlleScopes = requiredScopes.all { scope -> scopes.contains(scope) }

        if (!harAlleScopes) {
            log.info("Avvist: mangler ett eller flere påkrevde scopes. Har: $scopes, krever: $requiredScopes")
        }

        return harAlleScopes
    }
}
