package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("itest")
@Component
@Slf4j
public class AADSimpleMailTest implements AADMailClient {

    @Override
    public void sendMailViaClient(Message message) throws Exception {
        log.info("Sender melding til " + message.subject);
    }
}
