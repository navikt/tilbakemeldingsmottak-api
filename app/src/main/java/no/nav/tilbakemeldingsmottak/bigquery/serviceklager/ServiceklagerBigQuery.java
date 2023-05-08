package no.nav.tilbakemeldingsmottak.bigquery.serviceklager;

import com.google.api.client.util.DateTime;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceklagerBigQuery {

    public static final String TABLE_NAME = "serviceklager";
    private final Environment env;
    private final BigQuery bigQueryClient;
    @Value("${big_query_dataset}")
    private String dataset;

    private DateTime getDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        var epoch = localDateTime.atZone(ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli();
        return new DateTime(epoch);
    }

    public void insertServiceklage(Serviceklage serviceklage, ServiceklageEventTypeEnum eventType) {

        try {
            var map = new HashMap<String, Object>();
            map.put("event_type", eventType.toString());
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

            InsertAllRequest request = InsertAllRequest.newBuilder(TableId.of(dataset, TABLE_NAME)).addRow(map).build();

            log.info("Inserting rows into Big Query");

            if (Arrays.asList(env.getActiveProfiles()).contains("local")) {
                log.info("Skal ikke legge til rader i big query i lokal env");
                return;
            }

            var insertAllResponse = bigQueryClient.insertAll(request);
            if (insertAllResponse.hasErrors()) {
                insertAllResponse.getInsertErrors().forEach((k, v) -> log.error("Error inserting row: " + k + " " + v.toString()));
            }
        } catch (Exception e) {
            log.error("Error inserting into BigQuery", e);
        }
    }
}
