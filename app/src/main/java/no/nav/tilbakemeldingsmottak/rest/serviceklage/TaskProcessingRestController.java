package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.tilbakemeldingsmottak.api.TaskProcessingRestControllerApi;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.metrics.Metrics;
import no.nav.tilbakemeldingsmottak.model.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentDokumentService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentSkjemaService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.KlassifiserServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static no.nav.tilbakemeldingsmottak.util.OppgaveUtils.assertHarJournalpost;
import static no.nav.tilbakemeldingsmottak.util.OppgaveUtils.assertIkkeFerdigstilt;

@Slf4j
@ProtectedWithClaims(issuer = "azuread", claimMap = {"scp=defaultaccess serviceklage-klassifisering"})
@RestController
@RequiredArgsConstructor
public class TaskProcessingRestController implements TaskProcessingRestControllerApi {

    private final KlassifiserServiceklageService klassifiserServiceklageService;
    private final HentSkjemaService hentSkjemaService;
    private final HentDokumentService hentDokumentService;
    private final OpprettServiceklageValidator opprettServiceklageValidator;
    private final KlassifiserServiceklageValidator klassifiserServiceklageValidator;
    private final OppgaveConsumer oppgaveConsumer;

    private final String NEI = "Nei";

    @Transactional
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "klassifiserServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<KlassifiserServiceklageResponse> klassifiserServiceklage(@RequestParam String oppgaveId,
                                                                                   @RequestBody KlassifiserServiceklageRequest request) {
        log.info("Mottatt kall om å klassifisere serviceklage med oppgaveId={}", oppgaveId);

        if (NEI.equals(request.getFULGTBRUKERVEILEDNINGGOSYS()) || NEI.equals(request.getKOMMUNALBEHANDLING())) {
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
    @Override
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
    @Override
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "hentDokument"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<HentDokumentResponse> hentDokument(@PathVariable String oppgaveId) {
        HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
        assertIkkeFerdigstilt(hentOppgaveResponseTo);
        assertHarJournalpost(hentOppgaveResponseTo);

        HentDokumentResponse response = hentDokumentService.hentDokument(hentOppgaveResponseTo.getJournalpostId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
