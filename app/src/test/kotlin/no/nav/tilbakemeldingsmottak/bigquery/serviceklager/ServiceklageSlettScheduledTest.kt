package no.nav.tilbakemeldingsmottak.bigquery.serviceklager

import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.QueryJobConfiguration
import no.nav.tilbakemeldingsmottak.ApplicationTest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

internal class ServiceklageSlettScheduledTest : ApplicationTest() {

    @MockBean
    private lateinit var bigQueryClient: BigQuery

    @Autowired
    private lateinit var serviceklageSlettScheduled: ServiceklageSlettScheduled

    @Test
    fun testSlettServiceKlager() {
        // Gitt
        val slettOpprettedeServiceklager =
            "DELETE FROM `gcp-team-project-id.dataset.serviceklager` WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), opprettet_dato, DAY) >= 7 AND event_type='OPPRETT_SERVICEKLAGE'"
        val slettKlassifiserteServiceklager =
            "DELETE FROM `gcp-team-project-id.dataset.serviceklager` WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), avsluttet_dato, DAY) >= 7 AND event_type='KLASSIFISER_SERVICEKLAGE'"

        val slettOpprettedeServiceklagerQueryConfig =
            QueryJobConfiguration.newBuilder(slettOpprettedeServiceklager).build()
        val slettKlassifiserteServiceklagerQueryConfig =
            QueryJobConfiguration.newBuilder(slettKlassifiserteServiceklager).build()

        // Når
        serviceklageSlettScheduled.slettServiceKlager()

        // Så
        verify(bigQueryClient, times(1)).query(slettOpprettedeServiceklagerQueryConfig)
        verify(bigQueryClient, times(1)).query(slettKlassifiserteServiceklagerQueryConfig)
        verifyNoMoreInteractions(bigQueryClient)
    }
}
