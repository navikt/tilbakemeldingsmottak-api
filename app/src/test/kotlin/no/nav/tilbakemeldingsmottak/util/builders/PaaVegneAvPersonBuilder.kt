package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.PaaVegneAvPerson

class PaaVegneAvPersonBuilder {
    private var navn: String? = "Paal Vegne Personsen"
    private var personnummer: String? = "28898698736"

    fun build(
        navn: String? = this.navn,
        personnummer: String? = this.personnummer
    ) = PaaVegneAvPerson(
        navn = navn,
        personnummer = personnummer
    )
}