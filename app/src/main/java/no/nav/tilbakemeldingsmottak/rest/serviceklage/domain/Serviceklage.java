package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.BEHANDLES_SOM_SERVICEKLAGE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.ENHETSNUMMER_BEHANDLENDE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.ENHETSNUMMER_PAAKLAGET;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.FREMMET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.GJELDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNSENDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.JOURNALPOST_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGEN_GJELDER_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETEKST;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLAGETYPE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KLASSIFISERING_JSON;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.OPPRETTET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SCHEMA_VERSION;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SERVICEKLAGE_ID;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVARMETODE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.TEMA;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.UTFALL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.VEILEDNING;
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

    @Column(name = KLAGETYPE)
    private String klagetype;

    @Column(name = KLAGETEKST)
    private String klagetekst;

    @Column(name = BEHANDLES_SOM_SERVICEKLAGE)
    private String behandlesSomServiceklage;

    @Column(name = FREMMET_DATO)
    private LocalDateTime fremmetDato;

    @Column(name = INNSENDER)
    private String innsender;

    @Column(name = KANAL)
    private String kanal;

    @Column(name = ENHETSNUMMER_PAAKLAGET)
    private String enhetsnummerPaaklaget;

    @Column(name = ENHETSNUMMER_BEHANDLENDE)
    private String enhetsnummerBehandlende;

    @Column(name = GJELDER)
    private String gjelder;

    @Column(name = YTELSE)
    private String ytelse;

    @Column(name = TEMA)
    private String tema;

    @Column(name = VEILEDNING)
    private String veiledning;

    @Column(name = UTFALL)
    private String utfall;

    @Column(name = SVARMETODE)
    private String svarmetode;

    @Column(name = SVAR_IKKE_NOEDVENDIG)
    private String svarIkkeNoedvendig;

    @Column(name = SCHEMA_VERSION)
    private Long schemaVersion;

    @Column(name = KLASSIFISERING_JSON)
    private String klassifiseringJson;
}
