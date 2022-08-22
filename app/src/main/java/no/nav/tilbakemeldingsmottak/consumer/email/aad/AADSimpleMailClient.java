package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Profile("nais | spring")
@Component
public class AADSimpleMailClient implements AADMailClient {

    private static final Logger log = LoggerFactory.getLogger(AADSimpleMailClient.class);

    private final AADProperties aadProperties;

    private volatile AADToken token;

    @Autowired
    public AADSimpleMailClient(AADProperties aadProperties) {
        this.aadProperties = aadProperties;
    }

    public void sendMailViaClient(Message message) throws Exception {
        IGraphServiceClient graphClient = GraphServiceClient.builder()
                .authenticationProvider(new SimpleAuthProvider(getToken().getAccessToken()))
                .buildClient();
        graphClient.getLogger().setLoggingLevel(LoggerLevel.ERROR);

        graphClient.me()
                .sendMail(message, null)
                .buildRequest()
                .post();
    }

    private AADToken getToken() throws InterruptedException, ExecutionException, IOException {
        final LocalDateTime inTwoMinutes = LocalDateTime.now().plusMinutes(2L);
        if (token == null || token.getExpires().isBefore(inTwoMinutes)) {
            synchronized (this) {
                if (token == null || token.getExpires().isBefore(inTwoMinutes)) {
                    log.info("Renewing Azure token");
                    token = getNewAADToken();
                    log.info("New token expires at {}", token.getExpires());
                }
            }
        }
        return token;
    }


    private AADToken getNewAADToken() throws ExecutionException, InterruptedException, IOException {
        PublicClientApplication pca = PublicClientApplication.builder(aadProperties.getClientId()).
                authority(aadProperties.getAuthority()).
                build();

        Set<String> scopes = new HashSet<>();
        scopes.add("User.Read");

        UserNamePasswordParameters parameters =
                UserNamePasswordParameters.builder(
                                scopes,
                                aadProperties.getServiceuser(),
                                aadProperties.getPassword().toCharArray())
                        .build();

        IAuthenticationResult result = pca.acquireToken(parameters).get();
        return new AADToken(result.accessToken(), result.expiresOnDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
    }


}
