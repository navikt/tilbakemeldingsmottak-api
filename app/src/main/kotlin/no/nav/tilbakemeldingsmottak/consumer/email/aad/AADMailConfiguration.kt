package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.azure.core.credential.TokenCredential
import com.azure.identity.ClientSecretCredentialBuilder
import com.microsoft.graph.serviceclient.GraphServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("nais")
class AADMailConfiguration(private val aadProperties: AADProperties) {

    private val MICROSOFT_GRAPH_SCOPES: Set<String> = java.util.Set.of(
        "Mail.Send"
    )

    @Bean
    fun getGraphClient(): GraphServiceClient {

        return GraphServiceClient(getTokenCredentials(), *MICROSOFT_GRAPH_SCOPES.toTypedArray())
    }

    private fun getTokenCredentials(): TokenCredential {
        return ClientSecretCredentialBuilder()
            .tenantId(aadProperties.tenant)
            .clientId(aadProperties.clientId)
            .clientSecret(aadProperties.clientSecret)
            .build()
    }
}
