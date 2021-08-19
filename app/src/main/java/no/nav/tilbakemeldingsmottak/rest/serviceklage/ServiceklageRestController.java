package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import static no.nav.tilbakemeldingsmottak.config.Constants.AZURE_ISSUER;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST;
import static no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE;
import static no.nav.tilbakemeldingsmottak.util.OppgaveUtils.assertIkkeFerdigstilt;

import com.itextpdf.text.DocumentException;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Protected;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer;
import no.nav.tilbakemeldingsmottak.consumer.oppgave.domain.HentOppgaveResponseTo;
import no.nav.tilbakemeldingsmottak.exceptions.EksterntKallException;
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
import no.nav.tilbakemeldingsmottak.util.OidcUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final OidcUtils oidcUtils;

    private final String NEI = "Nei";

    @Transactional(dontRollbackOn = EksterntKallException.class)
    @PostMapping
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "opprettServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<OpprettServiceklageResponse>
            opprettServiceklage(@RequestBody OpprettServiceklageRequest request,
            @CookieValue(name = "selvbetjening-idtoken", required = false) String selvbetjening)
            throws DocumentException {

        log.info("Mottatt serviceklage via skjema på nav.no");
        boolean innlogget = oidcUtils.getSubject(selvbetjening) != null ? true : oidcUtils.getSubjectForIssuer(AZURE_ISSUER).isPresent();
        log.info("Bruker er innlogget " + innlogget);
        opprettServiceklageValidator.validateRequest(request);
        OpprettServiceklageResponse opprettServiceklageResponse = opprettServiceklageService.opprettServiceklage(request, innlogget);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(opprettServiceklageResponse);

    }

    @Transactional
    @PutMapping(value = "/klassifiser")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "klassifiserServiceklage"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<KlassifiserServiceklageResponse> klassifiserServiceklage(@RequestBody KlassifiserServiceklageRequest request,
                                                                                   @RequestParam String oppgaveId) throws DocumentException {
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

        HentSkjemaResponse response = hentSkjemaService.hentSkjema(hentOppgaveResponseTo.getJournalpostId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Transactional
    @GetMapping(value = "/hentdokument/{oppgaveId}")
    @Metrics(value = DOK_REQUEST, extraTags = {PROCESS_CODE, "hentDokument"}, percentiles = {0.5, 0.95}, histogram = true)
    public ResponseEntity<HentDokumentResponse> hentDokument(@PathVariable String oppgaveId) throws DocumentException {
        HentOppgaveResponseTo hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId);
        assertIkkeFerdigstilt(hentOppgaveResponseTo);

        HentDokumentResponse response = hentDokumentService.hentDokument(hentOppgaveResponseTo.getJournalpostId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
