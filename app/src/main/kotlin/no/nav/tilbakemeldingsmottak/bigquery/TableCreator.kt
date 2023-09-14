package no.nav.tilbakemeldingsmottak.bigquery

import com.google.cloud.bigquery.*
import jakarta.annotation.PostConstruct
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("nais")
class TableCreator(private val bigQueryClient: BigQuery) {

    @Value("\${big_query_dataset}")
    private val dataset: String? = null
    private fun getTableId(tableName: String): TableId {
        return TableId.of(dataset, tableName)
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    private fun createTable(tableName: String, schema: Schema) {
        val table = bigQueryClient.getTable(getTableId(tableName))
        if (table != null && table.exists()) {
            logger.info("Table {} already exists", table.tableId.table)
            return
        }

        try {
            logger.info("Creating table {}", tableName)
            val tableDefinition = StandardTableDefinition.of(schema)
            val tableInfo = TableInfo.newBuilder(getTableId(tableName), tableDefinition)
                .build()
            bigQueryClient.create(tableInfo)
            logger.info("Table {} created", tableInfo.tableId.table)
        } catch (e: Exception) {
            logger.error("Error creating table", e)
        }
    }

    @PostConstruct
    private fun initTables() {
        // Create table for serviceklager
        createTable(
            ServiceklagerBigQuery.TABLE_NAME, Schema.of(
                Field.newBuilder("event_type", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("serviceklage_id", StandardSQLTypeName.NUMERIC).build(),
                Field.newBuilder("journalpost_id", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("opprettet_dato", StandardSQLTypeName.TIMESTAMP).build(),
                Field.newBuilder("klagen_gjelder_id", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("klagetyper", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("gjelder_sosialhjelp", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("klagetekst", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("behandles_som_serviceklage", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("behandles_som_serviceklage_utdypning", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("fremmet_dato", StandardSQLTypeName.TIMESTAMP).build(),
                Field.newBuilder("innsender", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("kanal", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("kanal_utdypning", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("enhetsnummer_paaklaget", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("enhetsnummer_behandlende", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("gjelder", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("beskrivelse", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("ytelse", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("tema", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("tema_utdypning", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("veiledning", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("utfall", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("aarsak", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("tiltak", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("svarmetode", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("svarmetode_utdypning", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("avsluttet_dato", StandardSQLTypeName.TIMESTAMP).build(),
                Field.newBuilder("skjema_versjon", StandardSQLTypeName.NUMERIC).build(),
                Field.newBuilder("klassifisering_json", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("relatert", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("klagetype_utdypning", StandardSQLTypeName.STRING).build(),
                Field.newBuilder("innlogget", StandardSQLTypeName.BOOL).build()
            )
        )
    }
}
