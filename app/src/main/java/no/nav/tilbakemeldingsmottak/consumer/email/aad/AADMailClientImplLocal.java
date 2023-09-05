package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local | itest")
public class AADMailClientImplLocal implements AADMailClient {
    private static final Logger log = LoggerFactory.getLogger(AADMailClientImplLocal.class);
    @Value("${retry-config.send-mail.max-attempts}")
    private int mailMaxAttempts;

    @Override
    public void sendMailViaClient(Message message) {
        log.debug("Skal sende melding:" + message.subject + ", " + (message.body != null ? message.body.content : null));
        log.info("Sender ikke epost i local/test env");
    }

    @Override
    public void mailRecover(Exception e, Message message) throws Exception {
        log.error("Klarte ikke å sende epost etter {} forsøk", mailMaxAttempts, e);
        throw e;
    }

}
