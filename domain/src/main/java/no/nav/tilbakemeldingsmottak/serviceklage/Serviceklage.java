package no.nav.tilbakemeldingsmottak.serviceklage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static no.nav.tilbakemeldingsmottak.serviceklage.ServiceklageConstants.*;

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

    @Column(name = JOURNALPOST_ID)
    @Size(max = 11)
    private String journalpostId;

    @Column(name = OPPRETTET_DATO, nullable = false, updatable = false)
    private LocalDateTime opprettetDato;

    @Column(name = KLAGEN_GJELDER_ID)
    @Size(max = 15)
    private String klagenGjelderId;

    @Column(name = INNLOGGET)
    private Boolean innlogget;

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

    @Column(name = VEILEDNING)
    @Size(max = 2000)
    private String veiledning;

    @Column(name = AVSLUTTET_DATO)
    private LocalDateTime avsluttetDato;

    @Column(name = SKJEMA_VERSJON)
    private Long skjemaVersjon;

    @Column(name = KLASSIFISERING_JSON)
    @Size(max = 40000)
    private String klassifiseringJson;
}
