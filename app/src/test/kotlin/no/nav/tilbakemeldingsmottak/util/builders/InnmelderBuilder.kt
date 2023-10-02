package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.Innmelder

class InnmelderBuilder {
    private var navn: String? = "Innmelder Innmeldersen"
    private var telefonnummer: String? = null // Settes hvis innmelder ønsker sprivate var
    private var personnummer: String? = "01010096460"
    private var harFullmakt: Boolean? = null // Settes hvis det er på vegne av en annen privatperson
    private var rolle: String? = null // Settes hvis det er på vegne av en annen privatperson eller bedrift

    fun build(
        navn: String? = this.navn,
        telefonnummer: String? = this.telefonnummer,
        personnummer: String? = this.personnummer,
        harFullmakt: Boolean? = this.harFullmakt,
        rolle: String? = this.rolle
    ) = Innmelder(
        navn = navn,
        telefonnummer = telefonnummer,
        personnummer = personnummer,
        harFullmakt = harFullmakt,
        rolle = rolle
    )
}

