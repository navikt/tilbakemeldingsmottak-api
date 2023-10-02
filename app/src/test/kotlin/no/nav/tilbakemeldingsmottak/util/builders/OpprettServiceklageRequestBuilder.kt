package no.nav.tilbakemeldingsmottak.util.builders


import no.nav.tilbakemeldingsmottak.model.Innmelder
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageRequest.*
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvBedrift
import no.nav.tilbakemeldingsmottak.model.PaaVegneAvPerson

class OpprettServiceklageRequestBuilder {

    private var paaVegneAv: PaaVegneAv? = PaaVegneAv.PRIVATPERSON
    private var innmelder: Innmelder? = InnmelderBuilder().build()
    private var paaVegneAvPerson: PaaVegneAvPerson? = null
    private var paaVegneAvBedrift: PaaVegneAvBedrift? = null
    private var enhetsnummerPaaklaget: String? = null
    private var klagetyper: List<Klagetyper>? = listOf(Klagetyper.NAV_DIGITALE_TJENESTER)
    private var klagetypeUtdypning: String? = null // Settes hvis klagetyper inneholder ANNET
    private var gjelderSosialhjelp: GjelderSosialhjelp? = null // Settes hvis klagetyper inneholder LOKALT_NAV_KONTOR
    private var klagetekst: String? = "Dette er en klage"
    private var oenskerAaKontaktes: Boolean? = false

    fun build(
        paaVegneAv: PaaVegneAv? = this.paaVegneAv,
        innmelder: Innmelder? = this.innmelder,
        paaVegneAvPerson: PaaVegneAvPerson? = this.paaVegneAvPerson,
        paaVegneAvBedrift: PaaVegneAvBedrift? = this.paaVegneAvBedrift,
        enhetsnummerPaaklaget: String? = this.enhetsnummerPaaklaget,
        klagetyper: List<Klagetyper>? = this.klagetyper,
        klagetypeUtdypning: String? = this.klagetypeUtdypning,
        gjelderSosialhjelp: GjelderSosialhjelp? = this.gjelderSosialhjelp,
        klagetekst: String? = this.klagetekst,
        oenskerAaKontaktes: Boolean? = this.oenskerAaKontaktes
    ) = OpprettServiceklageRequest(
        paaVegneAv = paaVegneAv,
        innmelder = innmelder,
        paaVegneAvPerson = paaVegneAvPerson,
        paaVegneAvBedrift = paaVegneAvBedrift,
        enhetsnummerPaaklaget = enhetsnummerPaaklaget,
        klagetyper = klagetyper,
        klagetypeUtdypning = klagetypeUtdypning,
        gjelderSosialhjelp = gjelderSosialhjelp,
        klagetekst = klagetekst,
        oenskerAaKontaktes = oenskerAaKontaktes
    )

    fun asPrivatPerson(): OpprettServiceklageRequestBuilder {
        paaVegneAv = PaaVegneAv.PRIVATPERSON
        paaVegneAvPerson = null
        paaVegneAvBedrift = null
        return this
    }

    fun asPrivatPersonPaaVegneAv(): OpprettServiceklageRequestBuilder {
        paaVegneAv = PaaVegneAv.ANNEN_PERSON
        paaVegneAvPerson = PaaVegneAvPersonBuilder().build()
        paaVegneAvBedrift = null
        innmelder = InnmelderBuilder().build(rolle = "Advokat", harFullmakt = true)
        return this
    }

    fun asBedrift(): OpprettServiceklageRequestBuilder {
        paaVegneAv = PaaVegneAv.BEDRIFT
        paaVegneAvPerson = null
        paaVegneAvBedrift = PaaVegneAvBedriftBuilder().build()
        innmelder = InnmelderBuilder().build(rolle = "CEO")
        enhetsnummerPaaklaget = "1234"
        return this
    }

    fun withAnnetKlagetype(): OpprettServiceklageRequestBuilder {
        klagetyper = listOf(Klagetyper.ANNET)
        klagetypeUtdypning = "Dette er en utdypning"
        return this
    }

    fun withLokaltKontorKlagetype(): OpprettServiceklageRequestBuilder {
        klagetyper = listOf(Klagetyper.LOKALT_NAV_KONTOR)
        gjelderSosialhjelp = GjelderSosialhjelp.JA
        return this
    }


}

