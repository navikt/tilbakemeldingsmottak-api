package no.nav.tilbakemeldingsmottak.rest.ros.validation

import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosRequestHvemRoses
import no.nav.tilbakemeldingsmottak.rest.common.validation.RequestValidator
import org.springframework.stereotype.Component

@Component
class SendRosValidator : RequestValidator() {
    fun validateRequest(request: SendRosRequest) {
        isNotNull(request.hvemRoses, "hvemRoses")
        if (SendRosRequestHvemRoses.NAV_KONTOR == request.hvemRoses) {
            hasText(request.navKontor, "navKontor", " dersom hvemRoses=NAV_KONTOR")
        }
        hasText(request.melding, "melding")
    }
}
