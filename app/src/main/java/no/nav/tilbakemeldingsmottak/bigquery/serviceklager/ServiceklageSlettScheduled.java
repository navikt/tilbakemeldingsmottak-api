package no.nav.tilbakemeldingsmottak.bigquery.serviceklager;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("nais | itest")
public class ServiceklageSlettScheduled {
    private final BigQuery bigQueryClient;

    @Value("${big_query_dataset}")
    private String dataset;

    @Value("${gcp_team_project_id}")
    private String projectId;

    @Value("${cron.slettBigQueryServiceKlagerEldreEnn}")
    private String slettBigQueryServiceKlagerEldreEnn;

    private String slettEldreEnnQuery(String datoFelt, String eventType) {
        return String.format(
                "DELETE FROM `%s.%s.%s` " +
                        "WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), %s, DAY) >= %s " +
                        "AND event_type='%s'",
                projectId, dataset, ServiceklagerBigQuery.TABLE_NAME,
                datoFelt, slettBigQueryServiceKlagerEldreEnn,
                eventType
        );
    }

    @Scheduled(cron = "${cron.slettBigQueryServiceKlagerScheduled}")
    public void slettServiceKlager() {
        try {
            log.info("Sletter serviceklager eldre enn {} dager", slettBigQueryServiceKlagerEldreEnn);

            // Slett alle opprettede serviceklager som er eldre enn 1 uke
            var slettOpprettedeServiceklager = slettEldreEnnQuery("opprettet_dato", ServiceklageEventTypeEnum.OPPRETT_SERVICEKLAGE.value);
            log.info("Query for sletting av opprettede serviceklager: {}", slettOpprettedeServiceklager);

            var slettOpprettedeServiceklagerQueryConfig = QueryJobConfiguration.newBuilder(slettOpprettedeServiceklager).build();
            bigQueryClient.query(slettOpprettedeServiceklagerQueryConfig);

            // Slett alle klassifiserte serviceklager som er eldre enn 1 uke
            var slettKlassifiserteServiceklager = slettEldreEnnQuery("avsluttet_dato", ServiceklageEventTypeEnum.KLASSIFISER_SERVICEKLAGE.value);
            log.info("Query for sletting av klassifiserte serviceklager: {}", slettOpprettedeServiceklager);

            var slettKlassifiserteServiceklagerQueryConfig = QueryJobConfiguration.newBuilder(slettKlassifiserteServiceklager).build();
            bigQueryClient.query(slettKlassifiserteServiceklagerQueryConfig);

        } catch (Exception e) {
            log.error("Kunne ikke slette Big Query serviceklager eldre enn {} dager", slettBigQueryServiceKlagerEldreEnn, e);
        }


    }
}
