package no.nav.tilbakemeldingsmottak.consumer.norg2

import com.ninjasquad.springmockk.MockkSpyBean
import io.github.resilience4j.retry.RetryRegistry
import io.mockk.every
import io.mockk.verify
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentSkjemaService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("itest")
@SpringBootTest
class Norg2ServiceTest {

    @MockkSpyBean
    lateinit var norg2Consumer: Norg2Consumer

    @MockkSpyBean
    lateinit var hentSkjemaService: HentSkjemaService

    @Autowired
    lateinit var metricsRegistery: MeterRegistry

    @Autowired
    lateinit var retryRegistry: RetryRegistry

    @Test
    fun `retry config should be loaded correctly`() {
        val retry = retryRegistry.retry("norg2")

        assertEquals(3, retry.retryConfig.maxAttempts)
    }

    @Test
    @Disabled
    fun `should retry when fetch enheter fails`() {
        // Given
        every { norg2Consumer.hentEnheter() } throws RuntimeException("boom")
        every { hentSkjemaService.readSkjema() } returns HentSkjemaResponse()

        // When
        try {
            hentSkjemaService.hentSkjema("1")
        } catch (e: RuntimeException) {
            System.err.println(e.message);
        }

        // Then
        verify(exactly = 3) { norg2Consumer.hentEnheter() }
    }

}