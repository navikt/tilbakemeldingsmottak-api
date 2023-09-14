package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG_ANSWER
import no.nav.tilbakemeldingsmottak.model.KlassifiserServiceklageRequest
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_1
import no.nav.tilbakemeldingsmottak.util.NavKontorConstants.Companion.NAV_KONTOR_2
import java.time.LocalDate

class KlassifiserServiceklageRequestBuilder {

    private var BEHANDLES_SOM_SERVICEKLAGE: String = "Ja"
    private var BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING: String? = null
    private var FULGT_BRUKERVEILEDNING_GOSYS: String? = null
    private var KOMMUNAL_BEHANDLING: String? = null
    private var FREMMET_DATO: String? = LocalDate.now().toString()
    private var INNSENDER: String? = "Bruker selv som privatperson"
    private var KANAL: String? = "Annet"
    private var KANAL_UTDYPNING: String? = "Kanal utdypning"
    private var PAAKLAGET_ENHET_ER_BEHANDLENDE: String? = "Nei"
    private var ENHETSNUMMER_PAAKLAGET: String? = NAV_KONTOR_1
    private var ENHETSNUMMER_BEHANDLENDE: String? = NAV_KONTOR_2
    private var GJELDER: String? = "Gjelder én ytelse eller tjeneste"
    private var BESKRIVELSE: String? = "Bruker klager på service"
    private var YTELSE: String? = "AAP - Arbeidsavklaringspenger"
    private var RELATERT: String? = "EØS-saken,Åpningstider på NAV-kontoret"
    private var TEMA: String? = "Vente på NAV"
    private var VENTE: String? = "Saksbehandlingstid"
    private var TILGJENGELIGHET: String? = null
    private var INFORMASJON: String? = null
    private var VEILEDNING: String? = null
    private var TEMA_UTDYPNING: String? = "Tema utdypning"
    private var UTFALL: String? = "b) Regler/rutiner/frister er fulgt men NAV burde ivaretatt bruker bedre"
    private var AARSAK: String? = "Service har vært dårlig"
    private var TILTAK: String? = "Gi bedre service"
    private var SVARMETODE: String? = SVAR_IKKE_NOEDVENDIG_ANSWER
    private var SVAR_IKKE_NOEDVENDIG: String? = "Bruker ikke bedt om svar"
    private var SVARMETODE_UTDYPNING: String? = null
    private var KVITTERING: String? = "Nei"

    fun build(
        BEHANDLES_SOM_SERVICEKLAGE: String = this.BEHANDLES_SOM_SERVICEKLAGE,
        BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING: String? = this.BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING,
        FULGT_BRUKERVEILEDNING_GOSYS: String? = this.FULGT_BRUKERVEILEDNING_GOSYS,
        KOMMUNAL_BEHANDLING: String? = this.KOMMUNAL_BEHANDLING,
        FREMMET_DATO: String? = this.FREMMET_DATO,
        INNSENDER: String? = this.INNSENDER,
        KANAL: String? = this.KANAL,
        KANAL_UTDYPNING: String? = this.KANAL_UTDYPNING,
        PAAKLAGET_ENHET_ER_BEHANDLENDE: String? = this.PAAKLAGET_ENHET_ER_BEHANDLENDE,
        ENHETSNUMMER_PAAKLAGET: String? = this.ENHETSNUMMER_PAAKLAGET,
        ENHETSNUMMER_BEHANDLENDE: String? = this.ENHETSNUMMER_BEHANDLENDE,
        GJELDER: String? = this.GJELDER,
        BESKRIVELSE: String? = this.BESKRIVELSE,
        YTELSE: String? = this.YTELSE,
        RELATERT: String? = this.RELATERT,
        TEMA: String? = this.TEMA,
        VENTE: String? = this.VENTE,
        TILGJENGELIGHET: String? = this.TILGJENGELIGHET,
        INFORMASJON: String? = this.INFORMASJON,
        VEILEDNING: String? = this.VEILEDNING,
        TEMA_UTDYPNING: String? = this.TEMA_UTDYPNING,
        UTFALL: String? = this.UTFALL,
        AARSAK: String? = this.AARSAK,
        TILTAK: String? = this.TILTAK,
        SVARMETODE: String? = this.SVARMETODE,
        SVAR_IKKE_NOEDVENDIG: String? = this.SVAR_IKKE_NOEDVENDIG,
        SVARMETODE_UTDYPNING: String? = this.SVARMETODE_UTDYPNING,
        KVITTERING: String? = this.KVITTERING
    ): KlassifiserServiceklageRequest {
        return KlassifiserServiceklageRequest(
            BEHANDLES_SOM_SERVICEKLAGE = BEHANDLES_SOM_SERVICEKLAGE,
            BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING = BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING,
            FULGT_BRUKERVEILEDNING_GOSYS = FULGT_BRUKERVEILEDNING_GOSYS,
            KOMMUNAL_BEHANDLING = KOMMUNAL_BEHANDLING,
            FREMMET_DATO = FREMMET_DATO,
            INNSENDER = INNSENDER,
            KANAL = KANAL,
            KANAL_UTDYPNING = KANAL_UTDYPNING,
            PAAKLAGET_ENHET_ER_BEHANDLENDE = PAAKLAGET_ENHET_ER_BEHANDLENDE,
            ENHETSNUMMER_PAAKLAGET = ENHETSNUMMER_PAAKLAGET,
            ENHETSNUMMER_BEHANDLENDE = ENHETSNUMMER_BEHANDLENDE,
            GJELDER = GJELDER,
            BESKRIVELSE = BESKRIVELSE,
            YTELSE = YTELSE,
            RELATERT = RELATERT,
            TEMA = TEMA,
            VENTE = VENTE,
            TILGJENGELIGHET = TILGJENGELIGHET,
            INFORMASJON = INFORMASJON,
            VEILEDNING = VEILEDNING,
            TEMA_UTDYPNING = TEMA_UTDYPNING,
            UTFALL = UTFALL,
            AARSAK = AARSAK,
            TILTAK = TILTAK,
            SVARMETODE = SVARMETODE,
            SVAR_IKKE_NOEDVENDIG = SVAR_IKKE_NOEDVENDIG,
            SVARMETODE_UTDYPNING = SVARMETODE_UTDYPNING,
            KVITTERING = KVITTERING
        )
    }

    fun asNotServiceklage(): KlassifiserServiceklageRequestBuilder {
        BEHANDLES_SOM_SERVICEKLAGE = "Nei, annet"
        return this
    }

    fun asKommunalKlage(): KlassifiserServiceklageRequestBuilder {
        BEHANDLES_SOM_SERVICEKLAGE = "Nei, serviceklagen gjelder kommunale tjenester eller ytelser"
        KOMMUNAL_BEHANDLING = "Ja"
        return this
    }

    fun asForvaltningsklage(): KlassifiserServiceklageRequestBuilder {
        BEHANDLES_SOM_SERVICEKLAGE = "Nei, en forvaltningsklage"
        FULGT_BRUKERVEILEDNING_GOSYS = "Ja"
        return this

    }

}