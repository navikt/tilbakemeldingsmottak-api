package no.nav.tilbakemeldingsmottak.config


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.util.Base64

@Configuration
class SecurityConfig(
    @Value("\${auth.issuers.azuread.issuer-uri}") private val azureadIssuer: String,
    @Value("\${auth.issuers.azuread.jwk-set-uri}") private val azureadJwkUri: String,
    @Value("\${auth.issuers.tokenx.issuer-uri}") private val tokenxIssuer: String,
    @Value("\${auth.issuers.tokenx.jwk-set-uri}") private val tokenxJwkUri: String,
) {
    private val logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    // JSON mapper from Jackson 3 (tools.jackson)
    private val mapper = jacksonObjectMapper()

    // Create concrete decoders (one per issuer). These are beans so Spring can manage/refresh if needed.
    @Bean
    fun azureJwtDecoder(): JwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(azureadJwkUri).build()

    @Bean
    fun tokenxJwtDecoder(): JwtDecoder =
        NimbusJwtDecoder.withJwkSetUri(tokenxJwkUri).build()

    /**
     * Light-weight issuer extraction: parse token payload (no crypto/validation) to read "iss".
     * Returns null if it cannot parse.
     */
    private fun extractIssuer(tokenValue: String): String? {
        val parts = tokenValue.split(".")
        if (parts.size < 2) return null
        val payloadPart = try {
            Base64.getUrlDecoder().decode(parts[1])
        } catch (ex: IllegalArgumentException) {
            return null
        }
        val payload = try {
            String(payloadPart)
        } catch (ex: Exception) {
            return null
        }
        val node = try {
            mapper.readTree(payload)
        } catch (ex: Exception) {
            return null
        }
        return node.get("iss")?.asText()
    }

    /**
     * Security chain. We inject both decoder beans as parameters so we can delegate to them.
     */
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        azureJwtDecoder: JwtDecoder,
        tokenxJwtDecoder: JwtDecoder
    ): SecurityFilterChain {

        // Delegating JwtDecoder: choose concrete decoder by inspecting "iss" claim.
        val delegatingDecoder = object : JwtDecoder {
            override fun decode(token: String): Jwt {
                val iss = extractIssuer(token) ?: {
                    logger.info("Missing issuer (iss) in token")
                    throw BadCredentialsException("Missing issuer (iss) in token")
                }
                // Important: compare full issuer string that the IdP actually emits. Consider normalization.
                return when (iss) {
                    azureadIssuer -> azureJwtDecoder.decode(token)
                    tokenxIssuer -> tokenxJwtDecoder.decode(token)
                    else -> {
                        logger.info("Unknown issuer: $iss")
                        throw BadCredentialsException("Unknown issuer: $iss")
                    }
                }
            }
        }

        // Optional: convert "scope"/"scp" claims to GrantedAuthorities (adjust to your claim names)
        val jwtAuthConverter = JwtAuthenticationConverter().apply {
            val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
            // e.g., grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_") // default
            // grantedAuthoritiesConverter.setAuthoritiesClaimName("scope") // or "scp"
            setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        }

        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/isAlive", "/isReady", "/health/**", "/public/**").permitAll()
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { rs ->
                rs.jwt { jwt ->
                    // register our delegating decoder
                    jwt.decoder(delegatingDecoder)
                    jwt.jwtAuthenticationConverter(jwtAuthConverter)
                }
            }

        return http.build()
    }
}