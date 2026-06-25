package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.model.Innmelder
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageV2Request
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvBedrift
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvPerson

class OpprettServiceklageV2RequestBuilder {

    private var paaVegneAv: OpprettServiceklagePaaVegneAv? = PRIVATPERSON
    private var innmelder: Innmelder? = InnmelderBuilder().build()
    private var paaVegneAvPerson: PaaVegneAvPerson? = null
    private var paaVegneAvBedrift: PaaVegneAvBedrift? = null
    private var enhetsnummerPaaklaget: String? = null
    private var klagetekst: String? = "Dette er en klage"
    private var oenskerAaKontaktes: Boolean? = false

    fun build(
        paaVegneAv: OpprettServiceklagePaaVegneAv? = this.paaVegneAv,
        innmelder: Innmelder? = this.innmelder,
        paaVegneAvPerson: PaaVegneAvPerson? = this.paaVegneAvPerson,
        paaVegneAvBedrift: PaaVegneAvBedrift? = this.paaVegneAvBedrift,
        enhetsnummerPaaklaget: String? = this.enhetsnummerPaaklaget,
        klagetekst: String? = this.klagetekst,
        oenskerAaKontaktes: Boolean? = this.oenskerAaKontaktes
    ) = OpprettServiceklageV2Request(
        paaVegneAv = paaVegneAv,
        innmelder = innmelder,
        paaVegneAvPerson = paaVegneAvPerson,
        paaVegneAvBedrift = paaVegneAvBedrift,
        enhetsnummerPaaklaget = enhetsnummerPaaklaget,
        klagetekst = klagetekst,
        oenskerAaKontaktes = oenskerAaKontaktes
    )

    fun asPrivatPerson(): OpprettServiceklageV2RequestBuilder {
        paaVegneAv = PRIVATPERSON
        paaVegneAvPerson = null
        paaVegneAvBedrift = null
        return this
    }

    fun asPrivatPersonPaaVegneAv(): OpprettServiceklageV2RequestBuilder {
        paaVegneAv = ANNEN_PERSON
        paaVegneAvPerson = PaaVegneAvPersonBuilder().build()
        paaVegneAvBedrift = null
        innmelder = InnmelderBuilder().build(rolle = "Advokat", harFullmakt = true)
        return this
    }

    fun asBedrift(): OpprettServiceklageV2RequestBuilder {
        paaVegneAv = BEDRIFT
        paaVegneAvPerson = null
        paaVegneAvBedrift = PaaVegneAvBedriftBuilder().build()
        innmelder = InnmelderBuilder().build(rolle = "CEO")
        enhetsnummerPaaklaget = "1234"
        return this
    }
}
