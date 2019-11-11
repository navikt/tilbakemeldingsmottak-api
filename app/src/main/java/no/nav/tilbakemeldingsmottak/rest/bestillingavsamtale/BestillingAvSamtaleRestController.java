package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.Protected;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakFunctionalException;
import no.nav.tilbakemeldingsmottak.exceptions.AbstractTilbakemeldingsmottakTechnicalException;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.domain.BestillSamtaleResponse;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.service.BestillingAvSamtaleService;
import no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.validation.BestillSamtaleValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/bestilling-av-samtale")
public class BestillingAvSamtaleRestController {

    private final BestillingAvSamtaleService bestillingAvSamtaleService;
    private final BestillSamtaleValidator bestillSamtaleValidator;


    @Inject
    public BestillingAvSamtaleRestController(final BestillingAvSamtaleService bestillingAvSamtaleService) {
        this.bestillingAvSamtaleService = bestillingAvSamtaleService;
        this.bestillSamtaleValidator = new BestillSamtaleValidator();
    }

    @Transactional
    @PostMapping
    public ResponseEntity<BestillSamtaleResponse> sendRos(@RequestBody BestillSamtaleRequest request) throws MessagingException {
        try {
            bestillSamtaleValidator.validateRequest(request);
            bestillingAvSamtaleService.bestillSamtale(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BestillSamtaleResponse.builder().message("Samtale bestilt").build());
        } catch (AbstractTilbakemeldingsmottakFunctionalException e) {
            log.warn("bestillSamtale feilet funksjonelt. Feilmelding={}", e
                    .getMessage());
            throw e;
        } catch (AbstractTilbakemeldingsmottakTechnicalException e) {
            log.warn("bestillSamtale feilet teknisk. Feilmelding={}", e
                    .getMessage(), e);
            throw e;
        }
    }
}
