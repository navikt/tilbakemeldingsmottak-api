package no.nav.tilbakemeldingsmottak.util.builders

import no.nav.tilbakemeldingsmottak.domain.models.Serviceklage
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random


class ServiceklageBuilder {
    private var serviceklageId: Long? = Random(System.currentTimeMillis()).nextLong(1, 10000)
    private var journalpostId: String? = "12345"
    private var opprettetDato: LocalDateTime? = LocalDateTime.now()
    private var klagenGjelderId: String? = "28898698736"
    private var innlogget: Boolean? = true
    private var klagetyper: String? = "Telefon"
    private var klagetypeUtdypning: String? = null
    private var gjelderSosialhjelp: String? = null
    private var klagetekst: String? = "Tilbakemelding"
    private var behandlesSomServiceklage: String? = null
    private var behandlesSomServiceklageUtdypning: String? = null
    private var fremmetDato: LocalDate? = LocalDate.now()
    private var innsender: String? = "Bruker selv som privatperson"
    private var kanal: String? = "Serviceklageskjema på nav.no"
    private var kanalUtdypning: String? = null
    private var enhetsnummerPaaklaget: String? = null
    private var enhetsnummerBehandlende: String? = null
    private var gjelder: String? = null
    private var beskrivelse: String? = null
    private var ytelse: String? = null
    private var relatert: String? = null
    private var tema: String? = null
    private var temaUtdypning: String? = null
    private var utfall: String? = null
    private var aarsak: String? = null
    private var tiltak: String? = null
    private var svarmetode: String? = "Svar ikke nødvendig"
    private var svarmetodeUtdypning: String? = "Bruker ikke bedt om svar"
    private var veiledning: String? = null
    private var avsluttetDato: LocalDateTime? = null
    private var skjemaVersjon: Long? = null
    private var klassifiseringJson: String? = null
    private var oppgaveId: String? = "1234567"

    fun serviceklageId(serviceklageId: Long?) = apply { this.serviceklageId = serviceklageId }
    fun journalpostId(journalpostId: String?) = apply { this.journalpostId = journalpostId }
    fun opprettetDato(opprettetDato: LocalDateTime?) = apply { this.opprettetDato = opprettetDato }
    fun klagenGjelderId(klagenGjelderId: String?) = apply { this.klagenGjelderId = klagenGjelderId }
    fun innlogget(innlogget: Boolean?) = apply { this.innlogget = innlogget }
    fun klagetyper(klagetyper: String?) = apply { this.klagetyper = klagetyper }
    fun klagetypeUtdypning(klagetypeUtdypning: String?) = apply { this.klagetypeUtdypning = klagetypeUtdypning }
    fun gjelderSosialhjelp(gjelderSosialhjelp: String?) = apply { this.gjelderSosialhjelp = gjelderSosialhjelp }
    fun klagetekst(klagetekst: String?) = apply { this.klagetekst = klagetekst }
    fun behandlesSomServiceklage(behandlesSomServiceklage: String?) =
        apply { this.behandlesSomServiceklage = behandlesSomServiceklage }

    fun behandlesSomServiceklageUtdypning(behandlesSomServiceklageUtdypning: String?) =
        apply { this.behandlesSomServiceklageUtdypning = behandlesSomServiceklageUtdypning }

    fun fremmetDato(fremmetDato: LocalDate?) = apply { this.fremmetDato = fremmetDato }
    fun innsender(innsender: String?) = apply { this.innsender = innsender }
    fun kanal(kanal: String?) = apply { this.kanal = kanal }
    fun kanalUtdypning(kanalUtdypning: String?) = apply { this.kanalUtdypning = kanalUtdypning }
    fun enhetsnummerPaaklaget(enhetsnummerPaaklaget: String?) =
        apply { this.enhetsnummerPaaklaget = enhetsnummerPaaklaget }

    fun enhetsnummerBehandlende(enhetsnummerBehandlende: String?) =
        apply { this.enhetsnummerBehandlende = enhetsnummerBehandlende }

    fun gjelder(gjelder: String?) = apply { this.gjelder = gjelder }
    fun beskrivelse(beskrivelse: String?) = apply { this.beskrivelse = beskrivelse }
    fun ytelse(ytelse: String?) = apply { this.ytelse = ytelse }
    fun relatert(relatert: String?) = apply { this.relatert = relatert }
    fun tema(tema: String?) = apply { this.tema = tema }
    fun temaUtdypning(temaUtdypning: String?) = apply { this.temaUtdypning = temaUtdypning }
    fun utfall(utfall: String?) = apply { this.utfall = utfall }
    fun aarsak(aarsak: String?) = apply { this.aarsak = aarsak }
    fun tiltak(tiltak: String?) = apply { this.tiltak = tiltak }
    fun svarmetode(svarmetode: String?) = apply { this.svarmetode = svarmetode }
    fun svarmetodeUtdypning(svarmetodeUtdypning: String?) = apply { this.svarmetodeUtdypning = svarmetodeUtdypning }
    fun veiledning(veiledning: String?) = apply { this.veiledning = veiledning }
    fun avsluttetDato(avsluttetDato: LocalDateTime?) = apply { this.avsluttetDato = avsluttetDato }
    fun skjemaVersjon(skjemaVersjon: Long?) = apply { this.skjemaVersjon = skjemaVersjon }
    fun klassifiseringJson(klassifiseringJson: String?) = apply { this.klassifiseringJson = klassifiseringJson }
    fun oppgaveId(oppgaveId: String?) = apply { this.oppgaveId = oppgaveId }

    fun asKlassifisert() {
        behandlesSomServiceklage("Ja")
        enhetsnummerPaaklaget("0000")
        enhetsnummerBehandlende("0000")
        gjelder("Gjelder én ytelse eller tjeneste")
        beskrivelse("Kort beskrivelse")
        ytelse("AFP - Avtalefestet pensjon")
        relatert("Korona-saken")
        tema("Vente på NAV")
        temaUtdypning("Saksbehandlingstid")
        utfall("a) Regler/rutiner/frister er fulgt - NAV har ivaretatt bruker godt")
        avsluttetDato(LocalDateTime.now())
        klassifiseringJson(
            "{\"BEHANDLES_SOM_SERVICEKLAGE\":\"Ja\",\"BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING\":null,\"FULGT_BRUKERVEILEDNING_GOSYS\":null,\"KOMMUNAL_BEHANDLING\":null,\"FREMMET_DATO\":\"2023-10-20\",\"INNSENDER\":\"Bruker selv som privatperson\",\"KANAL\":\"Serviceklageskjema på nav.no\",\"KANAL_UTDYPNING\":null,\"PAAKLAGET_ENHET_ER_BEHANDLENDE\":\"Ja\",\"ENHETSNUMMER_PAAKLAGET\":\"Andre - 0000\",\"ENHETSNUMMER_BEHANDLENDE\":null,\"GJELDER\":\"Gjelder én ytelse eller tjeneste\",\"BESKRIVELSE\":\"Kort beskrivelse\",\"YTELSE\":\"AFP - Avtalefestet pensjon\",\"RELATERT\":\"Korona-saken\",\"TEMA\":\"Vente på NAV\",\"VENTE\":\"Saksbehandlingstid\",\"TILGJENGELIGHET\":null,\"INFORMASJON\":null,\"VEILEDNING\":null,\"TEMA_UTDYPNING\":null,\"UTFALL\":\"a) Regler/rutiner/frister er fulgt - NAV har ivaretatt bruker godt\",\"AARSAK\":null,\"TILTAK\":null,\"SVARMETODE\":\"Svar ikke nødvendig\",\"SVAR_IKKE_NOEDVENDIG\":\"Bruker ikke bedt om svar\",\"SVARMETODE_UTDYPNING\":null,\"KVITTERING\":\"Ja\"}"
        )
    }

    fun build(): Serviceklage {
        return Serviceklage(
            serviceklageId = serviceklageId,
            journalpostId = journalpostId,
            opprettetDato = opprettetDato,
            klagenGjelderId = klagenGjelderId,
            innlogget = innlogget,
            klagetyper = klagetyper,
            klagetypeUtdypning = klagetypeUtdypning,
            gjelderSosialhjelp = gjelderSosialhjelp,
            klagetekst = klagetekst,
            behandlesSomServiceklage = behandlesSomServiceklage,
            behandlesSomServiceklageUtdypning = behandlesSomServiceklageUtdypning,
            fremmetDato = fremmetDato,
            innsender = innsender,
            kanal = kanal,
            kanalUtdypning = kanalUtdypning,
            enhetsnummerPaaklaget = enhetsnummerPaaklaget,
            enhetsnummerBehandlende = enhetsnummerBehandlende,
            gjelder = gjelder,
            beskrivelse = beskrivelse,
            ytelse = ytelse,
            relatert = relatert,
            tema = tema,
            temaUtdypning = temaUtdypning,
            utfall = utfall,
            aarsak = aarsak,
            tiltak = tiltak,
            svarmetode = svarmetode,
            svarmetodeUtdypning = svarmetodeUtdypning,
            veiledning = veiledning,
            avsluttetDato = avsluttetDato,
            skjemaVersjon = skjemaVersjon,
            klassifiseringJson = klassifiseringJson,
            oppgaveId = oppgaveId
        )
    }
}