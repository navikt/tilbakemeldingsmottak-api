package no.nav.tilbakemeldingsmottak.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    companion object {
        const val BEARER_AUTH_SCHEME = "bearerAuth"
        private val PUBLIC_OPENAPI_PATHS = setOf("/isAlive", "/isReady", "/internal/selftest", "/health/status")
    }

    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    BEARER_AUTH_SCHEME,
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            .addSecurityItem(SecurityRequirement().addList(BEARER_AUTH_SCHEME))

    @Bean
    fun publicOperationsCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.paths.orEmpty()
                .filterKeys { it in PUBLIC_OPENAPI_PATHS }
                .values
                .flatMap { pathItem -> pathItem.readOperations() }
                .forEach { operation -> operation.security(emptyList()) }
        }
}
