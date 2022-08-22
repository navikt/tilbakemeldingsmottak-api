package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.extensions.Message;

public interface AADMailClient {

    void sendMailViaClient(Message message) throws Exception;
}
