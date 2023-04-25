package no.nav.tilbakemeldingsmottak.bigquery;

import com.google.cloud.bigquery.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("nais")
public class TableCreator {

    private final BigQuery bigQueryClient;

    private TableId getTableId(String tableName) {
        return TableId.of("serviceklager", tableName);
    }
    
    @PostConstruct
    private void initTables() {
        // Create table for serviceklager
        createTable("serviceklager", Schema.of(
                Field.newBuilder("serviceklage_id", StandardSQLTypeName.NUMERIC).setDescription("Unik id for serviceklagen").build(),
                Field.newBuilder("journalpost_id", StandardSQLTypeName.STRING).setDescription("Journalpost id for JOARK").build(),
                Field.newBuilder("opprettet_dato", StandardSQLTypeName.TIMESTAMP).setDescription("Datoen serviceklagen ble opprettet").build(),
                Field.newBuilder("klagen_gjelder_id", StandardSQLTypeName.STRING).setDescription("Organisasjonsnummer eller personnummer som klager gjelder").build(),
                Field.newBuilder("klagetyper", StandardSQLTypeName.STRING).setDescription("Komma-separert liste av klagetyper (Telefon, Lokalt Nav-kontor, NAVs digitale tjenester, Brev eller Annet").build(),
                Field.newBuilder("gjelder_sosialhjelp", StandardSQLTypeName.STRING).setDescription("Ja/Nei om det gjelder sosial hjelp").build(),
                Field.newBuilder("klagetekst", StandardSQLTypeName.STRING).setDescription("Klagetekst sendt inn fra bruker").build(),
                Field.newBuilder("behandles_som_serviceklage", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("behandles_som_serviceklage_utdypning", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("fremmet_dato", StandardSQLTypeName.TIMESTAMP).setDescription("FIXME").build(),
                Field.newBuilder("innsender", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("kanal", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("kanal_utdypning", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("enhetsnummer_paaklaget", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("enhetsnummer_behandlende", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("gjelder", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("beskrivelse", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("ytelse", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("tema", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("tema_utdypning", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("veiledning", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("utfall", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("aarsak", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("tiltak", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("svarmetode", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("svarmetode_utdypning", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("avsluttet_dato", StandardSQLTypeName.TIMESTAMP).setDescription("FIXME").build(),
                Field.newBuilder("skjema_versjon", StandardSQLTypeName.NUMERIC).setDescription("FIXME").build(),
                Field.newBuilder("klassifisering_json", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("relatert", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("klagetype_utdypning", StandardSQLTypeName.STRING).setDescription("FIXME").build(),
                Field.newBuilder("innlogget", StandardSQLTypeName.BOOL).setDescription("FIXME").build()
        ));
    }


    public void createTable(String tableName, Schema schema) {
        var table = bigQueryClient.getTable(getTableId(tableName));
        if (table != null && table.exists()) {
            log.info("Table {} already exists", table.getTableId().getTable());
            return;
        }

        try {
            log.info("Creating table {}", tableName);
            var tableDefinition = StandardTableDefinition.of(schema);
            var tableInfo = TableInfo.newBuilder(getTableId(tableName), tableDefinition)
                    .build();
            bigQueryClient.create(tableInfo);
            log.info("Table {} created", tableInfo.getTableId().getTable());
        } catch (Exception e) {
            log.error("Error creating table", e);
        }
    }

}
