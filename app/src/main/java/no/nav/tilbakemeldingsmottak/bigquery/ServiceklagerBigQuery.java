package no.nav.tilbakemeldingsmottak.bigquery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.TableId;
import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceklagerBigQuery {

    private final BigQuery bigQueryClient;

    public void insertServiceklage(Serviceklage serviceklage) {
        ObjectMapper objectMapper = new ObjectMapper();
        var map = objectMapper.convertValue(serviceklage, java.util.Map.class);

        InsertAllRequest request = InsertAllRequest.newBuilder(TableId.of("serviceklager", "serviceklager")).addRow(map).build();

        System.out.println("Inserting rows into table");
        System.out.println(request.getRows());

        bigQueryClient.insertAll(request);

    }
}
