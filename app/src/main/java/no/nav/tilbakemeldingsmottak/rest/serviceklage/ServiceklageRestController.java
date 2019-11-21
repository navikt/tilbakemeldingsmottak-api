package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
import no.nav.tilbakemeldingsmottak.exceptions.OidcContextException;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentDokumentService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentSkjemaService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.KlassifiserServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/serviceklage")
@RequiredArgsConstructor
public class ServiceklageRestController {

    private final OpprettServiceklageService opprettServiceklageService;
    private final KlassifiserServiceklageService klassifiserServiceklageService;
    private final HentSkjemaService hentSkjemaService;
    private final HentDokumentService hentDokumentService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final KlassifiserServiceklageValidator klassifiserServiceklageValidator;
    private final OppgaveConsumer oppgaveConsumer;


    @Transactional(dontRollbackOn = EksterntKallException.class)
    @PostMapping
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "opprettServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<OpprettServiceklageResponse> opprettServiceklage(@RequestBody OpprettServiceklageRequest request) throws DocumentException {
        log.info("Mottatt serviceklage via skjema på nav.no");
        opprettServiceklageValidator.validateRequest(request);
        OpprettServiceklageResponse opprettServiceklageResponse = opprettServiceklageService.opprettServiceklage(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(opprettServiceklageResponse);
    }

    @Transactional
    @PutMapping(value = "/klassifiser")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "klassifiserServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<KlassifiserServiceklageResponse> klassifiserServiceklage(@RequestBody KlassifiserServiceklageRequest request,
                                                                                   @RequestParam String oppgaveId) {
        String journalpostId = oppgaveConsumer.hentOppgave(oppgaveId).getJournalpostId();
        klassifiserServiceklageValidator.validateRequest(request, hentSkjemaService.hentSkjema(journalpostId));
        klassifiserServiceklageService.klassifiserServiceklage(request, journalpostId, oppgaveId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(KlassifiserServiceklageResponse.builder()
                        .message("Klassifisert serviceklage med journalpostId=" + journalpostId)
                        .build());
    }

    @Transactional
    @GetMapping(value = "/hentskjema/{journalpostId}")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "hentSkjema"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<HentSkjemaResponse> hentSkjema(@PathVariable String journalpostId) {
        HentSkjemaResponse response = hentSkjemaService.hentSkjema(journalpostId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Transactional
    @GetMapping(value = "/hentdokument/{journalpostId}")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "hentDokument"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<HentDokumentResponse> hentDokument(@PathVariable String journalpostId) {
        HentDokumentResponse response = hentDokumentService.hentDokument(journalpostId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
