package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Protected;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentDokumentService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentSkjemaService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.KlassifiserServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import no.nav.tilbakemeldingsmottak.serviceklage.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.serviceklage.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.serviceklage.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.serviceklage.KlassifiserServiceklageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static no.nav.tilbakemeldingsmottak.util.OppgaveUtils.assertIkkeFerdigstilt;
import static no.nav.tilbakemeldingsmottak.util.OppgaveUtils.assertHarJournalpost;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/taskserviceklage")
@RequiredArgsConstructor
public class TaskProcessingRestController {

    private final KlassifiserServiceklageService klassifiserServiceklageService;
    private final HentSkjemaService hentSkjemaService;
    private final HentDokumentService hentDokumentService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final KlassifiserServiceklageValidator klassifiserServiceklageValidator;
    private final OppgaveConsumer oppgaveConsumer;

    private final String NEI = "Nei";

    @Transactional
    @PutMapping(value = "/klassifiser")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "klassifiserServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<KlassifiserServiceklageResponse> klassifiserServiceklage(@RequestBody KlassifiserServiceklageRequest request,
                                                                                   @RequestParam String oppgaveId)  {
        log.info("Mottatt kall om å klassifisere serviceklage med oppgaveId={}", oppgaveId);

        if (NEI.equals(request.getFulgtBrukerveiledningGosys()) || NEI.equals(request.getKommunalBehandling())) {
            log.info("Videre behandling kreves, saksbehandler er informert og videresendt til Gosys.");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(KlassifiserServiceklageResponse.builder()
                            .message("Videre behandling kreves.")
                            .build());
        }

        HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
        log.info("Hentet oppgaveId={}, med versjonsnummer={}", oppgaveId, hentOppgaveResponseTo.getVersjon());

        assertIkkeFerdigstilt(hentOppgaveResponseTo);
        assertHarJournalpost(hentOppgaveResponseTo);

        klassifiserServiceklageValidator.validateRequest(request, hentSkjemaService.hentSkjema(hentOppgaveResponseTo.getJournalpostId()));
        klassifiserServiceklageService.klassifiserServiceklage(request, hentOppgaveResponseTo);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(KlassifiserServiceklageResponse.builder()
                        .message("Klassifisert serviceklage med journalpostId=" + hentOppgaveResponseTo.getJournalpostId())
                        .build());
    }

    @Transactional
    @GetMapping(value = "/hentskjema/{oppgaveId}")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "hentSkjema"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<HentSkjemaResponse> hentSkjema(@PathVariable String oppgaveId) {
        HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
        assertIkkeFerdigstilt(hentOppgaveResponseTo);
        assertHarJournalpost(hentOppgaveResponseTo);

        HentSkjemaResponse response = hentSkjemaService.hentSkjema(hentOppgaveResponseTo.getJournalpostId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Transactional
    @GetMapping(value = "/hentdokument/{oppgaveId}")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "hentDokument"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<HentDokumentResponse> hentDokument(@PathVariable String oppgaveId)  {
        HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
        assertIkkeFerdigstilt(hentOppgaveResponseTo);
        assertHarJournalpost(hentOppgaveResponseTo);

        HentDokumentResponse response = hentDokumentService.hentDokument(hentOppgaveResponseTo.getJournalpostId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
