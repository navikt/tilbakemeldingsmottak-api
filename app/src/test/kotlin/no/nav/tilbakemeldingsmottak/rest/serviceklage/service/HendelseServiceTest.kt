package no.nav.tilbakemeldingsmottak.rest.serviceklage.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import no.nav.tilbakemeldingsmottak.domain.models.Hendelse
import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import no.nav.tilbakemeldingsmottak.repository.HendelseRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class HendelseServiceTest {

    private val hendelseRepository = mockk<HendelseRepository>()
    private val hendelseService = HendelseService(hendelseRepository)

    @BeforeEach
    fun setup() {
        every { hendelseRepository.save(any()) } returns mockk<Hendelse>()
    }

    @Test
    fun `should create a serviceklage hendelse`() {
        // Given
        val journalpostId = "123"
        val serviceklage = Serviceklage(journalpostId = journalpostId)

        // When
        hendelseService.createServiceklage(serviceklage)

        // Then
        verify { hendelseRepository.save(match { it.hendelsetype == "OPPRETT_SERVICEKLAGE" && it.journalpostId == journalpostId }) }
    }

    @Test
    fun `should classify a serviceklage hendelse`() {
        // Given
        val journalpostId = "456"
        val serviceklage = Serviceklage(journalpostId = journalpostId)

        // When
        hendelseService.classifyServiceklage(serviceklage)

        // Then
        verify { hendelseRepository.save(match { it.hendelsetype == "KLASSIFISER_SERVICEKLAGE" && it.journalpostId == journalpostId }) }

    }

}
