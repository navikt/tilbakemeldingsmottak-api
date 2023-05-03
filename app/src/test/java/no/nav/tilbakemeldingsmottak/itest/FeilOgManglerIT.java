package no.nav.tilbakemeldingsmottak.itest;

import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest;
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static no.nav.tilbakemeldingsmottak.TestUtils.createMeldFeilOgManglerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeilOgManglerIT extends ApplicationTest {

    private static final String URL_FEIL_OG_MANGLER = "/rest/feil-og-mangler";

    @Test
    void happyPath() {
        MeldFeilOgManglerRequest request = createMeldFeilOgManglerRequest();
        HttpEntity<MeldFeilOgManglerRequest> requestEntity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<MeldFeilOgManglerResponse> response = restTemplate.exchange(URL_FEIL_OG_MANGLER, HttpMethod.POST, requestEntity, MeldFeilOgManglerResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
}
