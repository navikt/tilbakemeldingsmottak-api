package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import no.nav.tilbakemeldingsmottak.itest.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.Mockito.*;

public final class AADMailClientImplTest extends ApplicationTest {

    @SpyBean
    private AADMailClientImplLocal mailClient;

    @Test
    public void shouldRetrySendMailAndRecover() {
        // Given
        var message = new Message();
        doThrow(RuntimeException.class).when(mailClient).sendMailViaClient(message);

        // When
        mailClient.sendMailViaClient(message);

        // Then
        verify(mailClient, times(3)).sendMailViaClient(message);

    }


}