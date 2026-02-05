package no.nav.tilbakemeldingsmottak.consumer.pdl

import com.microsoft.graph.models.Message
import com.ninjasquad.springmockk.MockkSpyBean
import io.github.resilience4j.retry.RetryRegistry
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("itest")
@SpringBootTest
class PdlServiceTest {

    @MockkSpyBean
    lateinit var pdlClient: PdlClient

    @Autowired
    lateinit var pdlService: PdlService

    @Autowired
    lateinit var retryRegistry: RetryRegistry

    @Test
    fun `retry config should be loaded correctly`() {
        val retry = retryRegistry.retry("pdlGraphQl")

        assertEquals(3, retry.retryConfig.maxAttempts)
    }

    @Test
    @Disabled
    fun `should retry call to PDL`() {
        // Given
        every { pdlClient.performQuery(any()) } throws RuntimeException("boom")

        // When
        try {
            pdlService.hentPersonIdents(brukerId = "12345678901")
        } catch (e: RuntimeException) {
            ;
        }

        // Then
        verify(exactly = 3) { pdlClient.performQuery(any()) }
        verify(exactly = 1) { pdlClient.retryFailed(any(), any()) }
    }

}