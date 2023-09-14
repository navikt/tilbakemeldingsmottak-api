package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.Message
import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.SpyBean

internal class AADMailClientImplTest : ApplicationTest() {

    @SpyBean
    private lateinit var mailClient: AADMailClientImplLocal

    @Test
    fun `should retry send mail and recover`() {
        // Given
        val message = Message()
        doThrow(RuntimeException::class.java).`when`(mailClient).sendMailViaClient(message)

        // When
        try {
            mailClient.sendMailViaClient(message)
        } catch (e: Exception) {
            // Ignore
        }

        // Then
        verify(mailClient, times(3)).sendMailViaClient(message)
    }
}
