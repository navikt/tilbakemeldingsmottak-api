package no.nav.tilbakemeldingsmottak.bigquery.serviceklager

import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.QueryJobConfiguration
import no.nav.tilbakemeldingsmottak.ApplicationTest
import no.nav.tilbakemeldingsmottak.util.builders.ServiceklageBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDateTime

internal class ServiceklageSlettScheduledTest : ApplicationTest() {

    @MockBean
    private lateinit var bigQueryClient: BigQuery

    @Autowired
    private lateinit var serviceklageSlettScheduled: ServiceklageSlettScheduled

    @Test
    fun `Should delete serviceklager from database that has avsluttet_dato before cutoffDate`() {
        // Given
        val serviceklage1DayAgo =
            ServiceklageBuilder().avsluttetDato(LocalDateTime.now().minusDays(1)).serviceklageId(null).build()
        val serviceklageNotAvsluttet = ServiceklageBuilder().avsluttetDato(null).serviceklageId(null).build()
        val seviceklage100daysAgo =
            ServiceklageBuilder().avsluttetDato(LocalDateTime.now().minusDays(100)).serviceklageId(null).build()

        serviceklageRepository?.save(serviceklage1DayAgo)
        serviceklageRepository?.save(serviceklageNotAvsluttet)
        serviceklageRepository?.save(seviceklage100daysAgo)

        assertEquals(3, serviceklageRepository?.count())

        // When
        serviceklageSlettScheduled.deleteServiceKlager()

        // Then
        assertEquals(2, serviceklageRepository?.count())

        val all = serviceklageRepository?.findAll()
        assertNotNull(all?.find { it.serviceklageId == serviceklage1DayAgo.serviceklageId })
        assertNotNull(all?.find { it.serviceklageId == serviceklageNotAvsluttet.serviceklageId })
        assertNull(all?.find { it.serviceklageId == seviceklage100daysAgo.serviceklageId }, "Should be deleted")

    }


    @Test
    fun `Should call correct query to delete big query serviceklager`() {
        // Given
        val slettOpprettedeServiceklager =
            "DELETE FROM `gcp-team-project-id.dataset.serviceklager` WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), opprettet_dato, DAY) >= 7 AND event_type='OPPRETT_SERVICEKLAGE'"
        val slettKlassifiserteServiceklager =
            "DELETE FROM `gcp-team-project-id.dataset.serviceklager` WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), avsluttet_dato, DAY) >= 7 AND event_type='KLASSIFISER_SERVICEKLAGE'"

        val slettOpprettedeServiceklagerQueryConfig =
            QueryJobConfiguration.newBuilder(slettOpprettedeServiceklager).build()
        val slettKlassifiserteServiceklagerQueryConfig =
            QueryJobConfiguration.newBuilder(slettKlassifiserteServiceklager).build()

        // When
        serviceklageSlettScheduled.deleteBigQueryServiceKlager()

        // Then
        verify(bigQueryClient, times(1)).query(slettOpprettedeServiceklagerQueryConfig)
        verify(bigQueryClient, times(1)).query(slettKlassifiserteServiceklagerQueryConfig)
        verifyNoMoreInteractions(bigQueryClient)
    }
}
