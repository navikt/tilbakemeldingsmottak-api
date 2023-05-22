package no.nav.tilbakemeldingsmottak.consumer.email.aad;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserRequestBuilder;
import com.microsoft.graph.requests.UserSendMailRequest;
import com.microsoft.graph.requests.UserSendMailRequestBuilder;
import no.nav.tilbakemeldingsmottak.itest.ApplicationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class AADMailClientImplTest extends ApplicationTest {

    @Autowired
    @InjectMocks
    private AADMailClientImpl mailClient;

    @MockBean
    private GraphServiceClient graphClient;

    @Mock
    private UserRequestBuilder userRequestBuilder;

    @Mock
    private UserSendMailRequestBuilder sendMailRequestBuilder;

    @Mock
    private UserSendMailRequest userSendMailRequest;

    @BeforeEach
    public void setUp() {
        when(graphClient.users(any())).thenReturn(userRequestBuilder);
        when(userRequestBuilder.sendMail(ArgumentMatchers.any(UserSendMailParameterSet.class))).thenReturn(sendMailRequestBuilder);
        when(sendMailRequestBuilder.buildRequest()).thenReturn(userSendMailRequest);
    }


    @Test
    public void shouldRetrySendMailAndRecover() {
        // Given
        var message = new Message();

        doAnswer(invocation -> {
            throw new RuntimeException("Test exception");
        }).when(userSendMailRequest).post();

        // When
        mailClient.sendMailViaClient(message);

        // Then
        verify(userSendMailRequest, times(3)).post();

    }


}