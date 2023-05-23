package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.azure.core.http.HttpClient;
import com.azure.core.util.HttpClientOptions;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("nais")
public class AADMailConfiguration {

    private final AADProperties aadProperties;

    @Bean
    GraphServiceClient getGraphClient() {

        final TokenCredentialAuthProvider authenticationProvider = getTokenProvider();

        final var graphHttpClient =
                HttpClients.createDefault(authenticationProvider)
                        .newBuilder()
                        .build();

        return GraphServiceClient
                .builder()
                .httpClient(graphHttpClient)
                .buildClient();
    }

    private TokenCredentialAuthProvider getTokenProvider() {
        HttpClientOptions clientOptions = new HttpClientOptions();

        HttpClient azHttpClient = HttpClient.createDefault(clientOptions);

        var clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(aadProperties.getClientId())
                .clientSecret(aadProperties.getClientSecret())
                .tenantId(aadProperties.getTenant())
                .httpClient(azHttpClient)
                .build();

        return new TokenCredentialAuthProvider(clientSecretCredential);
    }
}
