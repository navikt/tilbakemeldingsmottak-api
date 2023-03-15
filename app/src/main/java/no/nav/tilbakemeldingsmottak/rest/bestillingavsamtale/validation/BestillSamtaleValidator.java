package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.validation;

import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest;
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator;

public class BestillSamtaleValidator extends RequestValidator {

    public void validateRequest(BestillSamtaleRequest request) {
        hasText(request.getFornavn(), "fornavn");
        hasText(request.getEtternavn(), "etternavn");
        isNotNull(request.getTidsrom(), "tidsrom");
        hasText(request.getTelefonnummer(), "telefonnummer");
    }

}
