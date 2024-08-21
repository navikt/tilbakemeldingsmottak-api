package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.azure.identity.ClientSecretCredential
import com.azure.identity.ClientSecretCredentialBuilder
import com.microsoft.graph.serviceclient.GraphServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("nais")
class AADMailConfiguration(private val aadProperties: AADProperties) {

    private val MICROSOFT_GRAPH_SCOPE_V2: String = "https://graph.microsoft.com/"

    private val MICROSOFT_GRAPH_SCOPES: Set<String> = java.util.Set.of(
        MICROSOFT_GRAPH_SCOPE_V2 + ".default",
        MICROSOFT_GRAPH_SCOPE_V2 + "Mail.Send"
    )

    @Bean
    fun getGraphClient(): GraphServiceClient {
        return GraphServiceClient(getClientCredentials())
    }

    private fun getClientCredentials(): ClientSecretCredential {
        return ClientSecretCredentialBuilder()
            .tenantId(aadProperties.tenant)
            .clientId(aadProperties.clientId)
            .clientSecret(aadProperties.clientSecret)
            .build()
    }
}
