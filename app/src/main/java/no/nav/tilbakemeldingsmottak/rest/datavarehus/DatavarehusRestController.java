package no.nav.tilbakemeldingsmottak.rest.datavarehus;

import com.azure.core.annotation.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Unprotected;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.rest.datavarehus.service.DatavarehusService;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

@Slf4j
// FIXME: Secure this endpoint
@Unprotected
@RestController
@RequestMapping("/rest/datavarehus")
@RequiredArgsConstructor
public class DatavarehusRestController {

    private final DatavarehusService datavarehusService;

    @Transactional
    @GetMapping("/serviceklage")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "datavarehus"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<List<Serviceklage>> hentServiceKlageDataForDatavarehus(@RequestParam("datoFra") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datoFra) {
        List<Serviceklage> serviceklageData = datavarehusService.hentServiceklageData(datoFra);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(serviceklageData);
    }
}
