package no.nav.tilbakemeldingsmottak.bigquery;

import com.google.api.client.util.DateTime;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceklagerBigQuery {

    private final BigQuery bigQueryClient;

    private ZoneOffset getOffset(LocalDateTime localDateTime) {
        return ZoneId.of("Europe/Oslo").getRules().getOffset(localDateTime);
    }

    private DateTime getDateTime(LocalDateTime localDateTime) {
        return new DateTime(localDateTime.toEpochSecond(getOffset(localDateTime)));
    }

    public void insertServiceklage(Serviceklage serviceklage) {

        try {
            var map = new HashMap<String, Object>();
            map.put("serviceklage_id", serviceklage.getServiceklageId());
            map.put("journalpost_id", serviceklage.getJournalpostId());
            map.put("opprettet_dato", getDateTime(serviceklage.getOpprettetDato()));
            map.put("klagen_gjelder_id", serviceklage.getKlagenGjelderId());
            map.put("klagetyper", serviceklage.getKlagetyper());
            map.put("gjelder_sosialhjelp", serviceklage.getGjelderSosialhjelp());
            map.put("klagetekst", serviceklage.getKlagetekst());
            map.put("behandles_som_serviceklage", serviceklage.getBehandlesSomServiceklage());
            map.put("behandles_som_serviceklage_utdypning", serviceklage.getBehandlesSomServiceklageUtdypning());
            map.put("fremmet_dato", getDateTime(serviceklage.getFremmetDato().atStartOfDay()));
            map.put("innsender", serviceklage.getInnsender());
            map.put("kanal", serviceklage.getKanal());
            map.put("kanal_utdypning", serviceklage.getKanalUtdypning());
            map.put("enhetsnummer_paaklaget", serviceklage.getEnhetsnummerPaaklaget());
            map.put("enhetsnummer_behandlende", serviceklage.getEnhetsnummerBehandlende());
            map.put("gjelder", serviceklage.getGjelder());
            map.put("beskrivelse", serviceklage.getBeskrivelse());
            map.put("ytelse", serviceklage.getYtelse());
            map.put("tema_utdypning", serviceklage.getTemaUtdypning());
            map.put("veiledning", serviceklage.getVeiledning());
            map.put("utfall", serviceklage.getUtfall());
            map.put("aarsak", serviceklage.getAarsak());
            map.put("tiltak", serviceklage.getTiltak());
            map.put("svarmetode", serviceklage.getSvarmetode());
            map.put("svarmetode_utdypning", serviceklage.getSvarmetodeUtdypning());
            map.put("avsluttet_dato", getDateTime(serviceklage.getAvsluttetDato()));
            map.put("skjema_versjon", serviceklage.getSkjemaVersjon());
            map.put("relatert", serviceklage.getRelatert());
            map.put("klagetype_utdypning", serviceklage.getKlagetypeUtdypning());
            map.put("innlogget", serviceklage.getInnlogget());

            log.info(map.toString());

            InsertAllRequest request = InsertAllRequest.newBuilder(TableId.of("serviceklager", "serviceklager")).addRow(map).build();

            log.info("Inserting rows into table");
            log.info(request.getRows().toString());

            var insertAllResponse = bigQueryClient.insertAll(request);
            if (insertAllResponse.hasErrors()) {
                insertAllResponse.getInsertErrors().forEach((k, v) -> log.error("Error inserting row: " + k + " " + v.toString()));
                log.error("insertAllResponse has errors");
            }
            log.info(insertAllResponse.toString());
        } catch (Exception e) {
            log.error("Error inserting into BigQuery", e);
        }
    }
}
