package no.nav.tilbakemeldingsmottak.bigquery.serviceklager

import com.google.api.client.util.DateTime
import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.BigQueryError
import com.google.cloud.bigquery.InsertAllRequest
import com.google.cloud.bigquery.TableId
import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class ServiceklagerBigQuery(private val env: Environment, private val bigQueryClient: BigQuery) {

    companion object {
        const val TABLE_NAME = "serviceklager"
    }

    @Value("\${big_query_dataset}")
    private val dataset: String? = null

    private val logger = LoggerFactory.getLogger(javaClass)

    private fun getDateTime(localDateTime: LocalDateTime?): DateTime? {
        if (localDateTime == null) {
            return null
        }
        val epoch = localDateTime.atZone(ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli()
        return DateTime(epoch)
    }

    fun insertServiceklage(serviceklage: Serviceklage, eventType: ServiceklageEventType) {
        try {
            val map = HashMap<String, Any?>()
            map["event_type"] = eventType.name
            map["serviceklage_id"] = serviceklage.serviceklageId
            map["journalpost_id"] = serviceklage.journalpostId
            map["opprettet_dato"] = getDateTime(serviceklage.opprettetDato)
            map["klagen_gjelder_id"] = serviceklage.klagenGjelderId
            map["klagetyper"] = serviceklage.klagetyper
            map["gjelder_sosialhjelp"] = serviceklage.gjelderSosialhjelp
            map["klagetekst"] = null
            map["behandles_som_serviceklage"] = serviceklage.behandlesSomServiceklage
            map["behandles_som_serviceklage_utdypning"] = serviceklage.behandlesSomServiceklageUtdypning
            map["fremmet_dato"] =
                getDateTime(if (serviceklage.fremmetDato != null) serviceklage.fremmetDato?.atStartOfDay() else null)
            map["innsender"] = serviceklage.innsender
            map["kanal"] = serviceklage.kanal
            map["kanal_utdypning"] = serviceklage.kanalUtdypning
            map["enhetsnummer_paaklaget"] = serviceklage.enhetsnummerPaaklaget
            map["enhetsnummer_behandlende"] = serviceklage.enhetsnummerBehandlende
            map["gjelder"] = serviceklage.gjelder
            map["beskrivelse"] = serviceklage.beskrivelse
            map["ytelse"] = serviceklage.ytelse
            map["tema"] = serviceklage.tema
            map["tema_utdypning"] = serviceklage.temaUtdypning
            map["veiledning"] = serviceklage.veiledning
            map["utfall"] = serviceklage.utfall
            map["aarsak"] = serviceklage.aarsak
            map["tiltak"] = serviceklage.tiltak
            map["svarmetode"] = serviceklage.svarmetode
            map["svarmetode_utdypning"] = serviceklage.svarmetodeUtdypning
            map["avsluttet_dato"] = getDateTime(serviceklage.avsluttetDato)
            map["skjema_versjon"] = serviceklage.skjemaVersjon
            map["klassifisering_json"] = serviceklage.klassifiseringJson
            map["relatert"] = serviceklage.relatert
            map["klagetype_utdypning"] = serviceklage.klagetypeUtdypning
            map["innlogget"] = serviceklage.innlogget
            map["oppgave_id"] = serviceklage.oppgaveId

            val request = InsertAllRequest.newBuilder(TableId.of(dataset, TABLE_NAME)).addRow(map).build()
            logger.info("Inserting rows into Big Query")

            if (env.activeProfiles.contains("local") || env.activeProfiles.contains("itest")) {
                logger.info("Skal ikke legge til rader i big query i lokal env")
                return
            }

            val insertAllResponse = bigQueryClient.insertAll(request)
            if (insertAllResponse.hasErrors()) {
                insertAllResponse.insertErrors.forEach { (k: Long, v: List<BigQueryError?>) ->
                    logger.error("Error inserting row: $k $v")
                }
            }
        } catch (e: Exception) {
            logger.error("Error inserting into BigQuery", e)
        }
    }
}
