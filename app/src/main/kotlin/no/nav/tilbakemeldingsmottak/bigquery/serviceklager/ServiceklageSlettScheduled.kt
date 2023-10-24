package no.nav.tilbakemeldingsmottak.bigquery.serviceklager

import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.QueryJobConfiguration
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Profile("nais | itest")
class ServiceklageSlettScheduled(
    private val bigQueryClient: BigQuery,
    private val serviceklagerBigQuery: ServiceklagerBigQuery,
    private val serviceklageRepository: ServiceklageRepository
) {

    @Value("\${big_query_dataset}")
    private val dataset: String? = null

    @Value("\${gcp_team_project_id}")
    private val projectId: String? = null

    @Value("\${cron.deleteBigQueryServiceKlagerOlderThan}")
    private val deleteBigQueryServiceKlagerOlderThan: String? = null

    @Value("\${cron.deleteServiceKlagerOlderThan}")
    private val deleteServiceKlagerOlderThan: String? = null

    private val logger = LoggerFactory.getLogger(javaClass)
    private fun slettEldreEnnQuery(datoFelt: String, eventType: String): String {
        return String.format(
            "DELETE FROM `%s.%s.%s` " +
                    "WHERE TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), %s, DAY) >= %s " +
                    "AND event_type='%s'",
            projectId, dataset, ServiceklagerBigQuery.TABLE_NAME,
            datoFelt, deleteBigQueryServiceKlagerOlderThan,
            eventType
        )
    }

    @Scheduled(cron = "\${cron.deleteServiceKlagerScheduled}")
    @Transactional
    fun deleteServiceKlager() {
        try {
            logger.info("Skal slette serviceklager fra databasen eldre enn {} dager", deleteServiceKlagerOlderThan)
            val cutoffDate = LocalDateTime.now().minusDays(deleteServiceKlagerOlderThan?.toLong() ?: 90)
            serviceklageRepository.deleteServiceklageOlderThan(cutoffDate)
            logger.info("Slettet serviceklager fra databasen eldre enn {} dager", deleteServiceKlagerOlderThan)
        } catch (e: Exception) {
            logger.error("Kunne ikke slette serviceklager eldre enn {} dager", deleteServiceKlagerOlderThan, e)
        }
    }

    @Scheduled(cron = "\${cron.deleteBigQueryServiceKlagerScheduled}")
    fun deleteBigQueryServiceKlager() {
        try {
            logger.info(
                "Skal slette serviceklager fra big query eldre enn {} dager",
                deleteBigQueryServiceKlagerOlderThan
            )

            // Slett alle opprettede serviceklager som er eldre enn 1 uke
            val slettOpprettedeServiceklager =
                slettEldreEnnQuery("opprettet_dato", ServiceklageEventType.OPPRETT_SERVICEKLAGE.name)

            logger.info("Query for sletting av opprettede serviceklager: {}", slettOpprettedeServiceklager)

            val slettOpprettedeServiceklagerQueryConfig =
                QueryJobConfiguration.newBuilder(slettOpprettedeServiceklager).build()
            bigQueryClient.query(slettOpprettedeServiceklagerQueryConfig)

            // Slett alle klassifiserte serviceklager som er eldre enn 1 uke
            val slettKlassifiserteServiceklager =
                slettEldreEnnQuery("avsluttet_dato", ServiceklageEventType.KLASSIFISER_SERVICEKLAGE.name)

            logger.info("Query for sletting av klassifiserte serviceklager: {}", slettOpprettedeServiceklager)

            val slettKlassifiserteServiceklagerQueryConfig =
                QueryJobConfiguration.newBuilder(slettKlassifiserteServiceklager).build()
            bigQueryClient.query(slettKlassifiserteServiceklagerQueryConfig)

            logger.info("Slettet serviceklager fra big query eldre enn {} dager", deleteBigQueryServiceKlagerOlderThan)

        } catch (e: Exception) {
            logger.error(
                "Kunne ikke slette Big Query serviceklager eldre enn {} dager",
                deleteBigQueryServiceKlagerOlderThan,
                e
            )
        }
    }

    @Scheduled(cron = "0 0 14 9 AUG ?")
    fun oppdaterServiceklager() {
        // Skal ikke kjøres årlig. Har blitt brukt til "manuelle" oppdateringer
        if (LocalDateTime.now().year == 2023) {
            val datoFra = LocalDateTime.now().minusMonths(4)
            try {
                logger.info("Oppdaterer serviceklager fra {}", datoFra)
                val serviceklager =
                    serviceklageRepository.findAllByOpprettetDatoAfterOrAvsluttetDatoAfter(datoFra, datoFra)
                for (serviceklage in serviceklager) {
                    serviceklagerBigQuery.insertServiceklage(
                        serviceklage,
                        ServiceklageEventType.OPPDATER_SERVICEKLAGE
                    )
                }
                logger.info("Oppdatert serviceklager fra {}", datoFra)
            } catch (ex: Exception) {
                logger.error("Kunne ikke oppdatere Big Query serviceklager fra {}", datoFra, ex)
            }
        }
    }
}
