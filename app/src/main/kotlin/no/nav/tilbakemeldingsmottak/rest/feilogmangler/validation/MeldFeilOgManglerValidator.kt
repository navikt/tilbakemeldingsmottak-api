package no.nav.tilbakemeldingsmottak.rest.feilogmangler.validation

import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator
import org.springframework.stereotype.Component

@Component
class MeldFeilOgManglerValidator : RequestValidator() {
    fun validateRequest(request: MeldFeilOgManglerRequest) {
        isNotNull(request.onskerKontakt, "onskerKontakt")
        if (request.onskerKontakt == true) {
            hasText(request.epost, "epost", " dersom onskerKontakt=true")
            isLegalEmail(request.epost, "epost", " dersom epost")
        }
        isNotNull(request.feiltype, "feiltype")
        hasText(request.melding, "melding")
        maxSize(request.melding, 10000, "melding")
    }
}
