package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.util.HttpClientOptions;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Profile("nais | spring")
@Component
public class AADMailClientImpl implements AADMailClient {

    private static final Logger log = LoggerFactory.getLogger(AADMailClientImpl.class);

    private final AADProperties aadProperties;

    @Autowired
    public AADMailClientImpl(AADProperties aadProperties) {
        this.aadProperties = aadProperties;
    }

    public void sendMailViaClient(Message message) {
        log.debug("Skal sende melding:" + message.subject + ", " + message.body.content);

        UserSendMailParameterSet sendMailParameterSet = UserSendMailParameterSet
                .newBuilder()
                .withMessage(message)
                .withSaveToSentItems(false)
                .build();

        GraphServiceClient graphClient = getGraphClient();
        graphClient.users(aadProperties.getServiceuser())
                .sendMail(sendMailParameterSet)
                .buildRequest()
                .post();

    }

    private Proxy getProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(aadProperties.getProxyHost(), aadProperties.getProxyPort()));
    }


    private TokenCredentialAuthProvider getTokenProviderViaProxy() {
        ProxyOptions proxyOptions = new ProxyOptions(
                ProxyOptions.Type.HTTP, new InetSocketAddress(aadProperties.getProxyHost(), aadProperties.getProxyPort()));

        HttpClientOptions clientOptions = new HttpClientOptions();
        clientOptions.setProxyOptions(proxyOptions);

        HttpClient azHttpClient = HttpClient.createDefault(clientOptions);

        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(aadProperties.getClientId())
                .clientSecret(aadProperties.getClientSecret())
                .tenantId(aadProperties.getTenant())
                .httpClient(azHttpClient)
                .build();

        return new TokenCredentialAuthProvider(clientSecretCredential);
    }


    private GraphServiceClient getGraphClient() {

        final TokenCredentialAuthProvider authenticationProvider = getTokenProviderViaProxy();

        final Proxy proxy = getProxy();

        final OkHttpClient graphHttpClient =
                HttpClients.createDefault(authenticationProvider)
                        .newBuilder()
                        .proxy(proxy)
                        .build();

        return GraphServiceClient
                        .builder()
                        .httpClient(graphHttpClient)
                        .buildClient();
    }
}