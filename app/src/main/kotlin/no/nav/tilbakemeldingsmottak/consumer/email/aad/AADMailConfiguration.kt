package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.azure.core.http.HttpClient
import com.azure.core.util.HttpClientOptions
import com.azure.identity.ClientSecretCredentialBuilder
import com.microsoft.graph.serviceclient.GraphServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("nais")
class AADMailConfiguration<ClientSecretCredential>(private val aadProperties: AADProperties) {
    @Bean
    fun getGraphClient(): GraphServiceClient {
        val clientSecretCredential = getClientSecretCredential()
        return GraphServiceClient(clientSecretCredential)
    }

    private fun getClientSecretCredential(): com.azure.identity.ClientSecretCredential? {
        val clientOptions = HttpClientOptions()
        val azHttpClient = HttpClient.createDefault(clientOptions)

        return ClientSecretCredentialBuilder()
            .clientId(aadProperties.clientId)
            .clientSecret(aadProperties.clientSecret)
            .tenantId(aadProperties.tenant)
            .httpClient(azHttpClient)
            .build()

    }
}
