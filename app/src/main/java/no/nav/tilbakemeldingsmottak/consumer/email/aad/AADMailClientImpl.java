package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AADMailClientImpl implements AADMailClient {

    private static final Logger log = LoggerFactory.getLogger(AADMailClientImpl.class);

    private final AADProperties aadProperties;
    private final GraphServiceClient graphClient;

    @Autowired
    Environment env;

    @Value("${retry-config.send-mail.max-attempts}")
    private int mailMaxAttempts;

    @Retryable(maxAttemptsExpression = "${retry-config.send-mail.max-attempts}", backoff = @Backoff(delayExpression = "${retry-config.send-mail.delay}", multiplierExpression = "${retry-config.send-mail.multiplier}"))
    public void sendMailViaClient(Message message) {
        log.debug("Skal sende melding:" + message.subject + ", " + (message.body != null ? message.body.content : null));

        if (Arrays.asList(env.getActiveProfiles()).contains("local")) {
            log.info("Skal ikke sende epost i local env");
            return;
        }

        UserSendMailParameterSet sendMailParameterSet = UserSendMailParameterSet
                .newBuilder()
                .withMessage(message)
                .withSaveToSentItems(false)
                .build();

        graphClient.users(aadProperties.getEmail())
                .sendMail(sendMailParameterSet)
                .buildRequest()
                .post();

    }

    @Recover
    public void mailRecover(Exception e, Message message) {
        log.error("Klarte ikke å sende epost etter {} forsøk. Send manuelt: {} {}", mailMaxAttempts, message.subject, message.body != null ? message.body.content : null, e);
    }


}
