package no.nav.tilbakemeldingsmottak.itest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage;

public class DatavarehusIT extends ApplicationTest {

    private static final String URL_DATAVAREHUS = "/rest/datavarehus/serviceklage";

    @Test
    void happyPath() {
        HttpEntity requestEntity = new HttpEntity(null, createHeaders());
        ResponseEntity<DatavarehusServiceklage[]> response = restTemplate.exchange(URL_DATAVAREHUS, HttpMethod.GET, requestEntity, DatavarehusServiceklage[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
