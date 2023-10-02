package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.azure.core.http.HttpClient
import com.azure.core.util.HttpClientOptions
import com.azure.identity.ClientSecretCredentialBuilder
import com.microsoft.graph.authentication.TokenCredentialAuthProvider
import com.microsoft.graph.httpcore.HttpClients
import com.microsoft.graph.requests.GraphServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("nais")
class AADMailConfiguration(private val aadProperties: AADProperties) {
    @Bean
    fun getGraphClient(): GraphServiceClient<*> {
        val authenticationProvider = getTokenProvider()
        val graphHttpClient = HttpClients.createDefault(authenticationProvider)
            .newBuilder()
            .build()
        return GraphServiceClient
            .builder()
            .httpClient(graphHttpClient)
            .buildClient()
    }

    private fun getTokenProvider(): TokenCredentialAuthProvider {
        val clientOptions = HttpClientOptions()
        val azHttpClient = HttpClient.createDefault(clientOptions)

        val clientSecretCredential = ClientSecretCredentialBuilder()
            .clientId(aadProperties.clientId)
            .clientSecret(aadProperties.clientSecret)
            .tenantId(aadProperties.tenant)
            .httpClient(azHttpClient)
            .build()

        return TokenCredentialAuthProvider(clientSecretCredential)
    }
}
