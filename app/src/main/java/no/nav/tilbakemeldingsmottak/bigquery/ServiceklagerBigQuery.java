package no.nav.tilbakemeldingsmottak.bigquery;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceklagerBigQuery {

    private final BigQuery bigQueryClient;

    public void insertServiceklage(Serviceklage serviceklage) {
        var map = new HashMap<String, Object>();
        map.put("serviceklage_id", serviceklage.getServiceklageId());
        map.put("journalpost_id", serviceklage.getJournalpostId());
        map.put("opprettet_dato", serviceklage.getOpprettetDato());
        map.put("klagen_gjelder_id", serviceklage.getKlagenGjelderId());
        map.put("klagetyper", serviceklage.getKlagetyper());
        map.put("gjelder_sosialhjelp", serviceklage.getGjelderSosialhjelp());
        map.put("klagetekst", serviceklage.getKlagetekst());
        map.put("behandles_som_serviceklage", serviceklage.getBehandlesSomServiceklage());
        map.put("behandles_som_serviceklage_utdypning", serviceklage.getBehandlesSomServiceklageUtdypning());
        map.put("fremmet_dato", serviceklage.getFremmetDato());
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
        map.put("avsluttet_dato", serviceklage.getAvsluttetDato());
        map.put("skjema_versjon", serviceklage.getSkjemaVersjon());
        map.put("relatert", serviceklage.getRelatert());
        map.put("klagetype_utdypning", serviceklage.getKlagetypeUtdypning());
        map.put("innlogget", serviceklage.getInnlogget());

        log.debug(map.toString());

        InsertAllRequest request = InsertAllRequest.newBuilder(TableId.of("serviceklager", "serviceklager")).addRow(map).build();

        System.out.println("Inserting rows into table");
        System.out.println(request.getRows());


        bigQueryClient.insertAll(request);

    }
}
