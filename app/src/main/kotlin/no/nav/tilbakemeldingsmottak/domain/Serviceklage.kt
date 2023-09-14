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
    var journalpostId: @Size(max = 11) String? = null,

    @Column(name = ServiceklageConstants.OPPRETTET_DATO, nullable = false, updatable = false)
    var opprettetDato: LocalDateTime? = null,

    @Column(name = ServiceklageConstants.KLAGEN_GJELDER_ID)
    var klagenGjelderId: @Size(max = 15) String? = null,

    @Column(name = ServiceklageConstants.INNLOGGET)
    var innlogget: Boolean? = null,

    @Column(name = ServiceklageConstants.KLAGETYPER)
    var klagetyper: @Size(max = 200) String? = null,

    @Column(name = ServiceklageConstants.KLAGETYPE_UTDYPNING)
    var klagetypeUtdypning: @Size(max = 1000) String? = null,

    @Column(name = ServiceklageConstants.GJELDER_SOSIALHJELP)
    var gjelderSosialhjelp: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.KLAGETEKST)
    var klagetekst: @Size(max = 20000) String? = null,

    @Column(name = ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE)
    var behandlesSomServiceklage: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING)
    var behandlesSomServiceklageUtdypning: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.FREMMET_DATO)
    var fremmetDato: LocalDate? = null,

    @Column(name = ServiceklageConstants.INNSENDER)
    var innsender: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.KANAL)
    var kanal: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.KANAL_UTDYPNING)
    var kanalUtdypning: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.ENHETSNUMMER_PAAKLAGET)
    var enhetsnummerPaaklaget: @Size(max = 4) String? = null,

    @Column(name = ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE)
    var enhetsnummerBehandlende: @Size(max = 4) String? = null,

    @Column(name = ServiceklageConstants.GJELDER)
    var gjelder: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.BESKRIVELSE)
    var beskrivelse: @Size(max = 20000) String? = null,

    @Column(name = ServiceklageConstants.YTELSE)
    var ytelse: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.RELATERT)
    var relatert: @Size(max = 200) String? = null,

    @Column(name = ServiceklageConstants.TEMA)
    var tema: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.TEMA_UTDYPNING)
    var temaUtdypning: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.UTFALL)
    var utfall: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.AARSAK)
    var aarsak: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.TILTAK)
    var tiltak: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.SVARMETODE)
    var svarmetode: @Size(max = 100) String? = null,

    @Column(name = ServiceklageConstants.SVARMETODE_UTDYPNING)
    var svarmetodeUtdypning: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.VEILEDNING)
    var veiledning: @Size(max = 2000) String? = null,

    @Column(name = ServiceklageConstants.AVSLUTTET_DATO)
    var avsluttetDato: LocalDateTime? = null,

    @Column(name = ServiceklageConstants.SKJEMA_VERSJON)
    var skjemaVersjon: Long? = null,

    @Column(name = ServiceklageConstants.KLASSIFISERING_JSON)
    var klassifiseringJson: @Size(max = 40000) String? = null
)
