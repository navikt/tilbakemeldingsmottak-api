package no.nav.tilbakemeldingsmottak.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "serviceklage")
data class Serviceklage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ServiceklageConstants.SERVICEKLAGE_ID, nullable = false)
    var serviceklageId: Long? = null,

    @Column(name = ServiceklageConstants.JOURNALPOST_ID)
    @field:Size(max = 11)
    var journalpostId: String? = null,

    @Column(name = ServiceklageConstants.OPPRETTET_DATO, nullable = false, updatable = false)
    var opprettetDato: LocalDateTime? = null,

    @Column(name = ServiceklageConstants.KLAGEN_GJELDER_ID)
    @field:Size(max = 15)
    var klagenGjelderId: String? = null,

    @Column(name = ServiceklageConstants.INNLOGGET)
    var innlogget: Boolean? = null,

    @Column(name = ServiceklageConstants.KLAGETYPER)
    @field:Size(max = 200)
    var klagetyper: String? = null,

    @Column(name = ServiceklageConstants.KLAGETYPE_UTDYPNING)
    @field:Size(max = 1000)
    var klagetypeUtdypning: String? = null,

    @Column(name = ServiceklageConstants.GJELDER_SOSIALHJELP)
    @field:Size(max = 100)
    var gjelderSosialhjelp: String? = null,

    @Column(name = ServiceklageConstants.KLAGETEKST)
    @field:Size(max = 20000)
    var klagetekst: String? = null,

    @Column(name = ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE)
    @field:Size(max = 100)
    var behandlesSomServiceklage: String? = null,

    @Column(name = ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING)
    @field:Size(max = 2000)
    var behandlesSomServiceklageUtdypning: String? = null,

    @Column(name = ServiceklageConstants.FREMMET_DATO)
    var fremmetDato: LocalDate? = null,

    @Column(name = ServiceklageConstants.INNSENDER)
    @field:Size(max = 100)
    var innsender: String? = null,

    @Column(name = ServiceklageConstants.KANAL)
    @field:Size(max = 100)
    var kanal: String? = null,

    @Column(name = ServiceklageConstants.KANAL_UTDYPNING)
    @field:Size(max = 2000)
    var kanalUtdypning: String? = null,

    @Column(name = ServiceklageConstants.ENHETSNUMMER_PAAKLAGET)
    @field:Size(max = 4)
    var enhetsnummerPaaklaget: String? = null,

    @Column(name = ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE)
    @field:Size(max = 4)
    var enhetsnummerBehandlende: String? = null,

    @Column(name = ServiceklageConstants.GJELDER)
    var gjelder: String? = null,

    @Column(name = ServiceklageConstants.BESKRIVELSE)
    @field:Size(max = 20000)
    var beskrivelse: String? = null,

    @Column(name = ServiceklageConstants.YTELSE)
    @field:Size(max = 100)
    var ytelse: String? = null,

    @Column(name = ServiceklageConstants.RELATERT)
    @field:Size(max = 200)
    var relatert: String? = null,

    @Column(name = ServiceklageConstants.TEMA)
    @field:Size(max = 100)
    var tema: String? = null,

    @Column(name = ServiceklageConstants.TEMA_UTDYPNING)
    @field:Size(max = 2000)
    var temaUtdypning: String? = null,

    @Column(name = ServiceklageConstants.UTFALL)
    @field:Size(max = 100)
    var utfall: String? = null,

    @Column(name = ServiceklageConstants.AARSAK)
    @field:Size(max = 2000)
    var aarsak: String? = null,

    @Column(name = ServiceklageConstants.TILTAK)
    @field:Size(max = 2000)
    var tiltak: String? = null,

    @Column(name = ServiceklageConstants.SVARMETODE)
    @field:Size(max = 100)
    var svarmetode: String? = null,

    @Column(name = ServiceklageConstants.SVARMETODE_UTDYPNING)
    @field:Size(max = 2000)
    var svarmetodeUtdypning: String? = null,

    @Column(name = ServiceklageConstants.VEILEDNING)
    @field:Size(max = 2000)
    var veiledning: String? = null,

    @Column(name = ServiceklageConstants.AVSLUTTET_DATO)
    var avsluttetDato: LocalDateTime? = null,

    @Column(name = ServiceklageConstants.SKJEMA_VERSJON)
    var skjemaVersjon: Long? = null,

    @Column(name = ServiceklageConstants.KLASSIFISERING_JSON)
    @field:Size(max = 40000)
    var klassifiseringJson: String? = null
)
