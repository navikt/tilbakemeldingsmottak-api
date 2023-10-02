package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest
import no.nav.tilbakemeldingsmottak.model.MeldFeilOgManglerRequest.Feiltype

class MeldFeilOgManglerRequestBuilder {

    private var onskerKontakt: Boolean? = false
    private var epost: String? = "innmelder@hotmail.com"
    private var feiltype: Feiltype? = Feiltype.TEKNISK_FEIL
    private var melding: String? = "Det er en feil på siden"
    fun build(
        onskerKontakt: Boolean? = this.onskerKontakt,
        epost: String? = this.epost,
        feiltype: Feiltype? = this.feiltype,
        melding: String? = this.melding
    ) = MeldFeilOgManglerRequest(
        onskerKontakt = onskerKontakt,
        epost = epost,
        feiltype = feiltype,
        melding = melding
    )
}