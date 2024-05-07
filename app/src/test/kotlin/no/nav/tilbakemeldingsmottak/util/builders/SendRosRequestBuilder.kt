package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.SendRosRequest
import no.nav.tilbakemeldingsmottak.model.SendRosRequestHvemRoses
import no.nav.tilbakemeldingsmottak.model.SendRosRequestHvemRoses.NAV_DIGITALE_TJENESTER
import no.nav.tilbakemeldingsmottak.model.SendRosRequestHvemRoses.NAV_KONTOR
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_1

class SendRosRequestBuilder {

    private var hvemRoses: SendRosRequestHvemRoses? = NAV_DIGITALE_TJENESTER
    private var navKontor: String? = null
    private var melding: String? = "Saksbehandleren var snill"

    fun build(
        hvemRoses: SendRosRequestHvemRoses? = this.hvemRoses,
        navKontor: String? = this.navKontor,
        melding: String? = this.melding
    ) = SendRosRequest(
        hvemRoses = hvemRoses,
        navKontor = navKontor,
        melding = melding
    )

    fun withNavKontor(): SendRosRequestBuilder {
        hvemRoses = NAV_KONTOR
        navKontor = NAV_KONTOR_1
        return this
    }
}