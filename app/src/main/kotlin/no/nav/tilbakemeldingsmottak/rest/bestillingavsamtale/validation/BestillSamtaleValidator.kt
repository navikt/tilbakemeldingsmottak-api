package no.nav.tilbakemeldingsmottak.rest.bestillingavsamtale.validation

import no.nav.tilbakemeldingsmottak.model.BestillSamtaleRequest
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator

class BestillSamtaleValidator : RequestValidator() {
    fun validateRequest(request: BestillSamtaleRequest) {
        hasText(request.fornavn, "fornavn")
        hasText(request.etternavn, "etternavn")
        isNotNull(request.tidsrom, "tidsrom")
        hasText(request.telefonnummer, "telefonnummer")
        isLegalTelephoneNumber(request.telefonnummer, "telefonnummer")
    }
}
