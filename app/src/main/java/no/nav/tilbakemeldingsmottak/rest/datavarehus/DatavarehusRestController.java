package no.nav.tilbakemeldingsmottak.rest.datavarehus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.tilbakemeldingsmottak.api.DatavarehusRestControllerApi;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage;
import no.nav.tilbakemeldingsmottak.rest.datavarehus.service.DatavarehusService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

@ProtectedWithClaims(issuer = "azuread", claimMap = {"scp=defaultaccess datavarehus"})
@RestController
@RequiredArgsConstructor
public class DatavarehusRestController implements DatavarehusRestControllerApi {
    
    private final DatavarehusService datavarehusService;

    @Transactional
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "datavarehus-serviceklager"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<List<DatavarehusServiceklage>> hentServiceKlageDataForDatavarehus(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime datoFra) {
        List<DatavarehusServiceklage> serviceklageData = datavarehusService.hentServiceklageData(datoFra);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(serviceklageData);
    }
}
