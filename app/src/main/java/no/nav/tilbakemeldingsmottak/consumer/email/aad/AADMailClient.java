package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;

public interface AADMailClient {

    void sendMailViaClient(Message message) throws Exception;
}
