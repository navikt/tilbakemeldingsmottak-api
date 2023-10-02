package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.PaaVegneAvBedrift

class PaaVegneAvBedriftBuilder {
    private var navn: String? = "PaaVegneAvBedrift Testesen"
    private var organisasjonsnummer: String? = "243602076"

    fun build(
        navn: String? = this.navn,
        organisasjonsnummer: String? = this.organisasjonsnummer
    ) = PaaVegneAvBedrift(
        navn = navn,
        organisasjonsnummer = organisasjonsnummer
    )
}
