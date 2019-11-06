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
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNSENDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.JOURNALPOST_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL_UTDYPNING;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGEN_GJELDER_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETYPER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLASSIFISERING_JSON;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.OPPRETTET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SCHEMA_VERSION;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SERVICEKLAGE_ID;
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
    private String journalpostId;

    @Column(name = OPPRETTET_DATO, nullable = false, updatable = false)
    private LocalDateTime opprettetDato;

    @Column(name = KLAGEN_GJELDER_ID)
    private String klagenGjelderId;

    @Column(name = KLAGETYPER)
    private String klagetyper;

    @Column(name = KLAGETEKST)
    private String klagetekst;

    @Column(name = BEHANDLES_SOM_SERVICEKLAGE)
    private String behandlesSomServiceklage;

    @Column(name = BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING)
    private String behandlesSomServiceklageUtdypning;

    @Column(name = FREMMET_DATO)
    private LocalDateTime fremmetDato;

    @Column(name = INNSENDER)
    private String innsender;

    @Column(name = KANAL)
    private String kanal;

    @Column(name = KANAL_UTDYPNING)
    private String kanalUtdypning;

    @Column(name = ENHETSNUMMER_PAAKLAGET)
    private String enhetsnummerPaaklaget;

    @Column(name = ENHETSNUMMER_BEHANDLENDE)
    private String enhetsnummerBehandlende;

    @Column(name = GJELDER)
    private String gjelder;

    @Column(name = BESKRIVELSE)
    private String beskrivelse;

    @Column(name = YTELSE)
    private String ytelse;

    @Column(name = TEMA)
    private String tema;

    @Column(name = TEMA_UTDYPNING)
    private String temaUtdypning;

    @Column(name = UTFALL)
    private String utfall;

    @Column(name = AARSAK)
    private String aarsak;

    @Column(name = TILTAK)
    private String tiltak;

    @Column(name = SVARMETODE)
    private String svarmetode;

    @Column(name = SVARMETODE_UTDYPNING)
    private String svarmetodeUtdypning;

    @Column(name = AVSLUTTET_DATO)
    private String avsluttetDato;

    @Column(name = SCHEMA_VERSION)
    private Long schemaVersion;

    @Column(name = KLASSIFISERING_JSON)
    private String klassifiseringJson;
}
