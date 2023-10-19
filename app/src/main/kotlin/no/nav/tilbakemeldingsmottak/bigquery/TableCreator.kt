package no.nav.tilbakemeldingsmottak.bigquery

import com.google.cloud.bigquery.*
import jakarta.annotation.PostConstruct
import no.nav.tilbakemeldingsmottak.bigquery.serviceklager.ServiceklagerBigQuery
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


// FIXME: Flyway supports BigQuery so should set up that instead
// https://documentation.red-gate.com/fd/google-bigquery-186679473.html
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
            addColumn(tableName, "oppgave_id", StandardSQLTypeName.STRING)
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

    fun addColumn(tableName: String, newColumnName: String?, type: StandardSQLTypeName) {
        try {
            val table = bigQueryClient.getTable(getTableId(tableName))
            val schema: Schema? = table.getDefinition<TableDefinition>().schema
            val fields = schema?.fields ?: return

            if (fields.any { it.name == newColumnName }) {
                logger.info("Column {} already exists", newColumnName)
                return
            }

            val newField = Field.of(newColumnName, type)

            val fieldList = mutableListOf<Field>()
            fields.forEach(fieldList::add)
            fieldList.add(newField)

            val newSchema = Schema.of(fieldList)

            // Update the table with the new schema
            val updatedTable = table.toBuilder().setDefinition(StandardTableDefinition.of(newSchema)).build()
            updatedTable.update()
            logger.info("{} column successfully added to table", newColumnName)
        } catch (e: BigQueryException) {
            logger.warn("{} column was not added", newColumnName, e)
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
