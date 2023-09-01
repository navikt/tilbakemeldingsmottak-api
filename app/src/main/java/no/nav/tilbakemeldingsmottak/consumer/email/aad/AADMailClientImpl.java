package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("nais")
public class AADMailClientImpl implements AADMailClient {

    private static final Logger log = LoggerFactory.getLogger(AADMailClientImpl.class);

    private final AADProperties aadProperties;
    private final GraphServiceClient graphClient;

    @Value("${retry-config.send-mail.max-attempts}")
    private int mailMaxAttempts;

    @Override
    public void sendMailViaClient(Message message) {
        log.debug("Skal sende melding:" + message.subject + ", " + (message.body != null ? message.body.content : null));

        UserSendMailParameterSet sendMailParameterSet = UserSendMailParameterSet
                .newBuilder()
                .withMessage(message)
                .withSaveToSentItems(false)
                .build();

        graphClient.users(aadProperties.getEmail())
                .sendMail(sendMailParameterSet)
                .buildRequest()
                .post();

        log.info("Epost sendt");
    }

    @Override
    public void mailRecover(Exception e, Message message) throws Exception {
        log.error("Klarte ikke å sende epost etter {} forsøk", mailMaxAttempts, e);
        throw e;
    }

}
