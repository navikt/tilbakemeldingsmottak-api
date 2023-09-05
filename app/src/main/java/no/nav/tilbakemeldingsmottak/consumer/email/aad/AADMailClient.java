package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface AADMailClient {

    @Retryable(retryFor = {Exception.class}, maxAttemptsExpression = "${retry-config.send-mail.max-attempts}", backoff = @Backoff(delayExpression = "${retry-config.send-mail.delay}", multiplierExpression = "${retry-config.send-mail.multiplier}"))
    void sendMailViaClient(Message message) throws Exception;

    @Recover
    void mailRecover(Exception e, Message message) throws Exception;

}
