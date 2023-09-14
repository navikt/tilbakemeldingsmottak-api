package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosRequest.HvemRoses
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_1

class SendRosRequestBuilder {

    private var hvemRoses: HvemRoses? = HvemRoses.NAV_DIGITALE_TJENESTER
    private var navKontor: String? = null
    private var melding: String? = "Saksbehandleren var snill"

    fun build(
        hvemRoses: HvemRoses? = this.hvemRoses,
        navKontor: String? = this.navKontor,
        melding: String? = this.melding
    ) = SendRosRequest(
        hvemRoses = hvemRoses,
        navKontor = navKontor,
        melding = melding
    )

    fun withNavKontor(): SendRosRequestBuilder {
        hvemRoses = HvemRoses.NAV_KONTOR
        navKontor = NAV_KONTOR_1
        return this
    }
}