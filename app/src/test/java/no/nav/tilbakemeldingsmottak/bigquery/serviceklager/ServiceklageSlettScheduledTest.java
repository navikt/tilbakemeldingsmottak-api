package no.nav.tilbakemeldingsmottak.bigquery.serviceklager;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import no.nav.tilbakemeldingsmottak.itest.ApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

public class ServiceklageSlettScheduledTest extends ApplicationTest {

    @MockBean
    private BigQuery bigQueryClient;

    @Autowired
    private ServiceklageSlettScheduled serviceklageSlettScheduled;

    @Test
    public void testSlettServiceKlager() throws InterruptedException {
        // Gitt
        String slettOpprettedeServiceklager = "DELETE FROM `gcp-team-project-id.dataset.serviceklager` WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), opprettet_dato, DAY) >= 7 AND event_type='OPPRETT_SERVICEKLAGE'";
        String slettKlassifiserteServiceklager = "DELETE FROM `gcp-team-project-id.dataset.serviceklager` WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), avsluttet_dato, DAY) >= 7 AND event_type='KLASSIFISER_SERVICEKLAGE'";

        QueryJobConfiguration slettOpprettedeServiceklagerQueryConfig = QueryJobConfiguration.newBuilder(slettOpprettedeServiceklager).build();
        QueryJobConfiguration slettKlassifiserteServiceklagerQueryConfig = QueryJobConfiguration.newBuilder(slettKlassifiserteServiceklager).build();

        // Når
        serviceklageSlettScheduled.slettServiceKlager();

        // Så
        verify(bigQueryClient, times(1)).query(slettOpprettedeServiceklagerQueryConfig);
        verify(bigQueryClient, times(1)).query(slettKlassifiserteServiceklagerQueryConfig);
        verifyNoMoreInteractions(bigQueryClient);
    }
}