package no.nav.tilbakemeldingsmottak.util.builders


import no.nav.tilbakemeldingsmottak.model.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageGjelderSosialhjelp.JA
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklageKlagetype.*
import no.nav.tilbakemeldingsmottak.model.OpprettServiceklagePaaVegneAv.*

class OpprettServiceklageRequestBuilder {

    private var paaVegneAv: OpprettServiceklagePaaVegneAv? = PRIVATPERSON
    private var innmelder: Innmelder? = InnmelderBuilder().build()
    private var paaVegneAvPerson: PaaVegneAvPerson? = null
    private var paaVegneAvBedrift: PaaVegneAvBedrift? = null
    private var enhetsnummerPaaklaget: String? = null
    private var klagetyper: List<OpprettServiceklageKlagetype>? = listOf(NAV_DIGITALE_TJENESTER)
    private var klagetypeUtdypning: String? = null // Settes hvis klagetyper inneholder ANNET
    private var gjelderSosialhjelp: OpprettServiceklageGjelderSosialhjelp? =
        null // Settes hvis klagetyper inneholder LOKALT_NAV_KONTOR
    private var klagetekst: String? = "Dette er en klage"
    private var oenskerAaKontaktes: Boolean? = false

    fun build(
        paaVegneAv: OpprettServiceklagePaaVegneAv? = this.paaVegneAv,
        innmelder: Innmelder? = this.innmelder,
        paaVegneAvPerson: PaaVegneAvPerson? = this.paaVegneAvPerson,
        paaVegneAvBedrift: PaaVegneAvBedrift? = this.paaVegneAvBedrift,
        enhetsnummerPaaklaget: String? = this.enhetsnummerPaaklaget,
        klagetyper: List<OpprettServiceklageKlagetype>? = this.klagetyper,
        klagetypeUtdypning: String? = this.klagetypeUtdypning,
        gjelderSosialhjelp: OpprettServiceklageGjelderSosialhjelp? = this.gjelderSosialhjelp,
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
        paaVegneAv = PRIVATPERSON
        paaVegneAvPerson = null
        paaVegneAvBedrift = null
        return this
    }

    fun asPrivatPersonPaaVegneAv(): OpprettServiceklageRequestBuilder {
        paaVegneAv = ANNEN_PERSON
        paaVegneAvPerson = PaaVegneAvPersonBuilder().build()
        paaVegneAvBedrift = null
        innmelder = InnmelderBuilder().build(rolle = "Advokat", harFullmakt = true)
        return this
    }

    fun asBedrift(): OpprettServiceklageRequestBuilder {
        paaVegneAv = BEDRIFT
        paaVegneAvPerson = null
        paaVegneAvBedrift = PaaVegneAvBedriftBuilder().build()
        innmelder = InnmelderBuilder().build(rolle = "CEO")
        enhetsnummerPaaklaget = "1234"
        return this
    }

    fun withAnnetKlagetype(): OpprettServiceklageRequestBuilder {
        klagetyper = listOf(ANNET)
        klagetypeUtdypning = "Dette er en utdypning"
        return this
    }

    fun withLokaltKontorKlagetype(): OpprettServiceklageRequestBuilder {
        klagetyper = listOf(LOKALT_NAV_KONTOR)
        gjelderSosialhjelp = JA
        return this
    }


}

