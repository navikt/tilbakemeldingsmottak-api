package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.AARSAK;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.AVSLUTTET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BESKRIVELSE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.ENHETSNUMMER_PAAKLAGET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.FREMMET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.GJELDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.GJELDER_SOSIALHJELP;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNSENDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.JOURNALPOST_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGEN_GJELDER_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETYPER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETYPE_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLASSIFISERING_JSON;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.OPPRETTET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.RELATERT;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SERVICEKLAGE_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SKJEMA_VERSJON;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVARMETODE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVARMETODE_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TEMA;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TEMA_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TILTAK;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.UTFALL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.YTELSE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "serviceklage")
public class Serviceklage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = SERVICEKLAGE_ID, nullable = false)
    private Long serviceklageId;

    @Column(name = JOURNALPOST_ID, nullable = false)
    @Size(max = 11)
    private String journalpostId;

    @Column(name = OPPRETTET_DATO, nullable = false, updatable = false)
    private LocalDateTime opprettetDato;

    @Column(name = KLAGEN_GJELDER_ID)
    @Size(max = 15)
    private String klagenGjelderId;

    @Column(name = KLAGETYPER)
    @Size(max = 200)
    private String klagetyper;

    @Column(name = KLAGETYPE_UTDYPNING)
    @Size(max = 1000)
    private String klagetypeUtdypning;

    @Column(name = GJELDER_SOSIALHJELP)
    @Size(max = 100)
    private String gjelderSosialhjelp;

    @Column(name = KLAGETEKST)
    @Size(max = 20000)
    private String klagetekst;

    @Column(name = BEHANDLES_SOM_SERVICEKLAGE)
    @Size(max = 100)
    private String behandlesSomServiceklage;

    @Column(name = BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING)
    @Size(max = 2000)
    private String behandlesSomServiceklageUtdypning;

    @Column(name = FREMMET_DATO)
    private LocalDate fremmetDato;

    @Column(name = INNSENDER)
    @Size(max = 100)
    private String innsender;

    @Column(name = KANAL)
    @Size(max = 100)
    private String kanal;

    @Column(name = KANAL_UTDYPNING)
    @Size(max = 2000)
    private String kanalUtdypning;

    @Column(name = ENHETSNUMMER_PAAKLAGET)
    @Size(max = 4)
    private String enhetsnummerPaaklaget;

    @Column(name = ENHETSNUMMER_BEHANDLENDE)
    @Size(max = 4)
    private String enhetsnummerBehandlende;

    @Column(name = GJELDER)
    @Size(max = 100)
    private String gjelder;

    @Column(name = BESKRIVELSE)
    @Size(max = 20000)
    private String beskrivelse;

    @Column(name = YTELSE)
    @Size(max = 100)
    private String ytelse;

    @Column(name = RELATERT)
    @Size(max = 200)
    private String relatert;

    @Column(name = TEMA)
    @Size(max = 100)
    private String tema;

    @Column(name = TEMA_UTDYPNING)
    @Size(max = 2000)
    private String temaUtdypning;

    @Column(name = UTFALL)
    @Size(max = 100)
    private String utfall;

    @Column(name = AARSAK)
    @Size(max = 2000)
    private String aarsak;

    @Column(name = TILTAK)
    @Size(max = 2000)
    private String tiltak;

    @Column(name = SVARMETODE)
    @Size(max = 100)
    private String svarmetode;

    @Column(name = SVARMETODE_UTDYPNING)
    @Size(max = 2000)
    private String svarmetodeUtdypning;

    @Column(name = AVSLUTTET_DATO)
    private LocalDateTime avsluttetDato;

    @Column(name = SKJEMA_VERSJON)
    private Long skjemaVersjon;

    @Column(name = KLASSIFISERING_JSON)
    @Size(max = 40000)
    private String klassifiseringJson;
}
