package no.nav.tilbakemeldingsmottak.rest.serviceklage

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.tilbakemeldingsmottak.api.TaskProcessingRestControllerApi
import no.nav.tilbakemeldingsmottak.consumer.oppgave.OppgaveConsumer
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.DOK_REQUEST
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels.PROCESS_CODE
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import no.nav.tilbakemeldingsmottak.model.HentDokumentResponse
import no.nav.tilbakemeldingsmottak.model.HentSkjemaResponse
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageResponse
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentDokumentService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentSkjemaService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.KlassifiserServiceklageService
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator
import no.nav.tilbakemeldingsmottak.util.OppgaveUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@ProtectedWithClaims(issuer = "azuread", claimMap = ["scp=defaultaccess serviceklage-klassifisering"])
@RestController
class TaskProcessingRestController(
    private val klassifiserServiceklageService: KlassifiserServiceklageService,
    private val hentSkjemaService: HentSkjemaService,
    private val hentDokumentService: HentDokumentService,
    private val klassifiserServiceklageValidator: KlassifiserServiceklageValidator,
    private val oppgaveConsumer: OppgaveConsumer
) : TaskProcessingRestControllerApi {

    private val log = LoggerFactory.getLogger(javaClass)

    private val NEI = "Nei"

    @Transactional
    @Metrics(
        value = DOK_REQUEST,
        extraTags = [PROCESS_CODE, "klassifiserServiceklage"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun klassifiserServiceklage(
        @RequestParam oppgaveId: String,
        @RequestBody klassifiserServiceklageRequest: KlassifiserServiceklageRequest
    ): ResponseEntity<KlassifiserServiceklageResponse> {
        log.info("Mottatt kall om å klassifisere serviceklage med oppgaveId={}", oppgaveId)
        if (NEI == klassifiserServiceklageRequest.FULGT_BRUKERVEILEDNING_GOSYS || NEI == klassifiserServiceklageRequest.KOMMUNAL_BEHANDLING) {
            log.info("Videre behandling kreves, saksbehandler er informert og videresendt til Gosys.")
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(KlassifiserServiceklageResponse("Videre behandling kreves."))
        }

        val hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId)
        log.info("Hentet oppgaveId={}, med versjonsnummer={}", oppgaveId, hentOppgaveResponseTo.versjon)

        OppgaveUtils.assertIkkeFerdigstilt(hentOppgaveResponseTo)
        OppgaveUtils.assertHarJournalpost(hentOppgaveResponseTo)

        klassifiserServiceklageValidator.validateRequest(
            klassifiserServiceklageRequest,
            hentSkjemaService.hentSkjema(hentOppgaveResponseTo.journalpostId!!)
        )
        klassifiserServiceklageService.klassifiserServiceklage(klassifiserServiceklageRequest, hentOppgaveResponseTo)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(KlassifiserServiceklageResponse("Klassifisert serviceklage med journalpostId=" + hentOppgaveResponseTo.journalpostId))
    }

    @Transactional
    @Metrics(
        value = DOK_REQUEST, extraTags = [PROCESS_CODE, "hentSkjema"], percentiles = [0.5, 0.95], histogram = true
    )
    override fun hentSkjema(@PathVariable oppgaveId: String): ResponseEntity<HentSkjemaResponse> {
        val hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId)

        OppgaveUtils.assertIkkeFerdigstilt(hentOppgaveResponseTo)
        OppgaveUtils.assertHarJournalpost(hentOppgaveResponseTo)

        val response = hentSkjemaService.hentSkjema(hentOppgaveResponseTo.journalpostId!!)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }

    @Transactional
    @Metrics(
        value = DOK_REQUEST,
        extraTags = [PROCESS_CODE, "hentDokument"],
        percentiles = [0.5, 0.95],
        histogram = true
    )
    override fun hentDokument(@PathVariable oppgaveId: String): ResponseEntity<HentDokumentResponse> {
        val hentOppgaveResponseTo = oppgaveConsumer.hentOppgave(oppgaveId)

        OppgaveUtils.assertIkkeFerdigstilt(hentOppgaveResponseTo)
        OppgaveUtils.assertHarJournalpost(hentOppgaveResponseTo)

        val response = hentOppgaveResponseTo.journalpostId?.let { hentDokumentService.hentDokument(it) }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response)
    }

}
