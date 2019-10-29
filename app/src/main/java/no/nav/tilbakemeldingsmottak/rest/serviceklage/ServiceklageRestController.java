package no.nav.tilbakemeldingsmottak.rest.serviceklage;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentDokumentResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.KlassifiserServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.OpprettServiceklageResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentDokumentService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.HentSkjemaService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.KlassifiserServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.service.OpprettServiceklageService;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.KlassifiserServiceklageValidator;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.validation.OpprettServiceklageValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private final OpprettServiceklageValidator opprettServiceklageValidator = new OpprettServiceklageValidator();
    private final KlassifiserServiceklageValidator klassifiserServiceklageValidator = new KlassifiserServiceklageValidator();


    @Transactional
    @PostMapping
    public ResponseEntity<OpprettServiceklageResponse> opprettServiceklage(@RequestBody OpprettServiceklageRequest request) throws DocumentException {
        try {
            opprettServiceklageValidator.validateRequest(request);
            Serviceklage serviceklage = opprettServiceklageService.opprettServiceklage(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(OpprettServiceklageResponse.builder()
                            .message("Serviceklage opprettet")
                            .serviceklageId(serviceklage.getServiceklageId().toString())
                            .journalpostId(serviceklage.getJournalpostId())
                            .oppgaveId(serviceklage.getOppgaveId())
                            .build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("opprettServiceklage feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("opprettServiceklage feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }

    @Transactional
    @PutMapping(value = "/{journalpostId}/klassifiser")
    public ResponseEntity<KlassifiserServiceklageResponse> klassifiserServiceklage(@RequestBody KlassifiserServiceklageRequest request, @PathVariable String journalpostId) {
        try {
            klassifiserServiceklageValidator.validateRequest(request);
            klassifiserServiceklageService.klassifiserServiceklage(request, journalpostId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(KlassifiserServiceklageResponse.builder().message("Klassifisert serviceklage med journalpostId=" + journalpostId).build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("klassifiserServiceklage feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("klassifiserServiceklage feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }

    @Transactional
    @GetMapping(value = "hentskjema/{journalpostId}")
    public ResponseEntity<HentSkjemaResponse> hentSkjema(@PathVariable String journalpostId) {
        try {
            HentSkjemaResponse response = hentSkjemaService.hentSkjema(journalpostId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("hentSkjema feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("hentSkjema feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }

    @Transactional
    @GetMapping(value = "/hentdokument/{journalpostId}")
    public ResponseEntity<HentDokumentResponse> hentDokument(@PathVariable String journalpostId, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            HentDokumentResponse response = hentDokumentService.hentDokument(journalpostId, authorizationHeader);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("hentDokument feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("hentDokument feilet teknisk. Feilmelding={}", e
                    .getMessage());
            throw e;
        }
    }
}
